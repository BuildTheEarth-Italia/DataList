/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (PermsLister.kt) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.listers

import net.milkbowl.vault.permission.Permission
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.Bukkit.getServer

internal class PermsLister(private val permission: Permission) {
    enum class Type {
        GROUPS,
        PERMISSIONS
    }

    fun getJSON(type: Type) : JsonArray {
        return when (type) {
            Type.GROUPS -> listGroups()
            Type.PERMISSIONS -> listPermissions()
        }
    }

    private fun listPermissions(): JsonArray {
        return JsonArray()
    }

    private fun listGroups(): JsonArray {
        //array di output
        val out = JsonArray()

        //foreach per ogni gruppo
        permission.groups.forEach {
            //array di output pe singolo permesso
            val permOut = JsonObject()
            val permMembers = JsonArray()

            //ciclo per inserire i player nei gruppi
            getServer().offlinePlayers.forEach { op ->
                if(permission.playerInGroup(null, op, it))
                    permMembers.add(op.name)
            }

            //salvo il nome
            permOut.addProperty("name", it)

            //salvo i membri
            permOut.add("members", permMembers)

            //salvo nella lista di output
            out.add(permOut)
        }

        //ritorno la lista
        return out
    }
}