/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (OnlinePlayersRequestHandler.kt) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.bteitalia.datalist.listers.PlayerLister
import it.bteitalia.datalist.server.RequestHandler

internal class OnlinePlayersRequestHandler : RequestHandler() {
    override fun onIncomingRequest(httpExchange: HttpExchange) {
        val out = JsonObject()

        //json di player online
        out.add("online", PlayerLister().getJSON(PlayerLister.Type.ONLINE))

        //invio i dati
        flushData(out.toString())
    }
}