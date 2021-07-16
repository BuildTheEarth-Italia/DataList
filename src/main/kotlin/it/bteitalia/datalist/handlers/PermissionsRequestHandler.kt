/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (PermissionsRequestHandler.kt) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.bteitalia.datalist.DataList
import it.bteitalia.datalist.listers.PermsLister
import it.bteitalia.datalist.listers.PermsLister.Type
import it.bteitalia.datalist.server.RequestHandler
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit.getServer

class PermissionsRequestHandler : RequestHandler() {
    //permissions
    private val p: Permission? = try {
        getServer().servicesManager.getRegistration(Permission::class.java)!!.provider
    } catch (ex: Throwable) {
        DataList.getInstance().printError("Impossibile caricare i permessi, il plugin Vault non è installato.")

        // Rimuovo il percorso
        try {
            removeThis()
        } catch (ex: IllegalArgumentException) {
        }

        null
    }

    override fun onIncomingRequest(exchange: HttpExchange) {
        //oggetto di out
        val out = JsonObject()

        // lister. è safe perché se p è null questo blocco non viene mai eseguito
        val list = try {
            PermsLister(p!!).getJSON(Type.GROUPS)
        } catch (ex: NullPointerException) {
            removeThis()
            abort(500)
            return
        }

        //aggiungo all'output
        out.add("groups", list)

        flushData(out.toString())
    }

    private fun removeThis() {
        DataList.getInstance().config["output.path.permissions"] = false
        DataList.getInstance().saveConfig()

        DataList.getInstance().httpServer.removeContext(
            DataList.getInstance().config.getString("output.path.permissions")
        )
    }
}