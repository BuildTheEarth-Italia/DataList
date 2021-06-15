/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (PermsRequestHandler.kt) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.bteitalia.datalist.server.RequestHandler
import it.bteitalia.datalist.listers.PermsLister
import it.bteitalia.datalist.listers.PermsLister.Type
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit.getServer

class PermsRequestHandler : RequestHandler() {
    //permissions
    var p = getServer().servicesManager.getRegistration(Permission::class.java)!!.provider

    override fun onIncomingRequest(exchange: HttpExchange) {
        //oggetto di out
        val out = JsonObject()

        //lister
        val list = PermsLister(p).getJSON(Type.GROUPS)

        //aggiungo all'output
        out.add("groups", list)

        flushData(out.toString())
    }
}