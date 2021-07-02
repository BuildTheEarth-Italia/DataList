/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (PlaytimeLister.kt) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.listers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import it.bteitalia.datalist.DataList
import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import kotlin.collections.HashMap


@Suppress("unused")
internal class PlaytimeLister : Listener {
    fun getJSON(): JsonArray {
        //oggetto di out
        val out = JsonArray()

        for (player in Bukkit.getOfflinePlayers()) {
            val playtime: Int =
                if (player.player != null)
                    getPlaytimeFromOnlinePlayer(player.player)
                else
                    getPlaytimeFromList(player.uniqueId)

            // Se playtime è 0 ignoro
            if (playtime <= 0)
                continue

            // Creo oggetto
            val jsonPlayerEntry = JsonObject()
            jsonPlayerEntry.addProperty("name", player.name)
            jsonPlayerEntry.addProperty("ticks", playtime)

            out.add(jsonPlayerEntry)
        }

        //ritorno l'array
        return out
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLogout(evt: PlayerQuitEvent) {
        playtime[evt.player.uniqueId] = getPlaytimeFromOnlinePlayer(evt.player)
        updateFile()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLogin(evt: PlayerJoinEvent) {
        playtime[evt.player.uniqueId] = getPlaytimeFromOnlinePlayer(evt.player)
        updateFile()
    }

    private fun getPlaytimeFromList(uuid: UUID): Int {
        return playtime[uuid] ?: 0
    }

    private fun updateFile() {
        val file = DataList.getInstance().dataFolder.resolve("playtime.json")

        // Controllo che esista
        if (!file.exists())
            file.createNewFile()

        // salvo nel file nell'hashmap
        val toSave = JsonObject()

        playtime.forEach { (uuid, value) ->
            toSave.addProperty(uuid.toString(), value)
        }

        val writer = FileWriter(file)
        writer.write(toSave.toString())
        writer.close()
    }

    private fun getPlaytimeFromOnlinePlayer(player: Player): Int {
        return player.getStatistic(
            if (DataList.getInstance().version < 1.13)
                Statistic.valueOf("PLAY_ONE_TICK")
            else
                Statistic.valueOf("PLAY_ONE_MINUTE")
        )
    }

    companion object {
        @JvmStatic
        private val playtime = HashMap<UUID, Int>()

        init {
            val file = DataList.getInstance().dataFolder.resolve("playtime.json")

            // Controllo che esista
            if (!file.exists())
                file.createNewFile()

            // Leggo il file nell'hashmap
            try {
                val parser = JsonParser().parse(FileReader(file))

                parser.asJsonObject.entrySet().forEach { (key, value) ->
                    val uuid = UUID.fromString(key)
                    val ticks = value.asInt

                    playtime[uuid] = ticks
                }
            } catch (e: Exception) {
            }
        }
    }
}