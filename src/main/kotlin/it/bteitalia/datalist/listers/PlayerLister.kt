/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (PlayerLister.kt) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.listers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.entity.Player

internal class PlayerLister {
    enum class Type {
        ONLINE,
        OFFLINE
    }

    fun getJSON(type: Type): JsonArray {
        //oggetto di out
        val out = JsonArray()

        //itero i player online
        Bukkit.getOfflinePlayers().forEach {
            if ((type == Type.OFFLINE && it.isOnline) || (type == Type.ONLINE && !it.isOnline)) return@forEach

            //aggiungo il player
            out.add(getJSON(it as Player))
        }

        //ritorno l'array
        return out
    }

    fun getJSON(player: Player): JsonObject {
        //oggetto in cui inserire i dati
        val entry = JsonObject()

        //metto il nome
        entry.addProperty("name", player.name)

        //metto la posizione se player
        val pos = JsonObject()
        pos.addProperty("x", player.location.x)
        pos.addProperty("y", player.location.y)
        pos.addProperty("z", player.location.z)
        pos.addProperty("world", player.location.world!!.name)
        entry.add("position", pos)


        //metto l'esperienza
        val exp = JsonObject()
        exp.addProperty("level", player.level)
        exp.addProperty("percentage", player.exp * 100)
        entry.add("experience", exp)

        //metto il livello di cibo
        entry.addProperty("foodLevel", player.foodLevel)

        //metto il livello di salute
        entry.addProperty("healthLevel", player.health)

        return entry
    }
}