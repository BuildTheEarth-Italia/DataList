/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (PointsRequestHandler.kt) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.bteitalia.datalist.DataList
import it.bteitalia.datalist.listers.PlayerLister
import it.bteitalia.datalist.listers.PointsLister
import it.bteitalia.datalist.server.RequestHandler

internal class PointsRequestHandler : RequestHandler() {
    companion object {
        private val defaultScoreboardName = DataList.getInstance().config.getString("defaultScoreboardName")
    }

    override fun onIncomingRequest(httpExchange: HttpExchange) {
        val out = JsonObject()

        // Ottengo il nome dello scoreboard
        val pointsScoreboardName =  queryParameters["name"] ?: defaultScoreboardName

        //json di player online
        out.add(pointsScoreboardName, PointsLister().getJSON(pointsScoreboardName))

        //invio i dati
        flushData(out.toString())
    }
}