/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (RootRequestHandler.java) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package it.bteitalia.datalist.handlers

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.bteitalia.datalist.DataList
import it.bteitalia.datalist.server.RequestHandler
import java.io.IOException
import java.util.*

class RootRequestHandler : RequestHandler() {
    companion object {
        private val URIs = JsonArray()

        init {
            // Prendo la lista degli endpoints
            val endpoints = DataList.getInstance().config
                .getConfigurationSection("output.path")

            endpoints.getKeys(true).forEach { path ->
                // Prendo il valore e controllo che sia una stringa
                val urlOrEnabled = endpoints[path] as? String ?: return@forEach

                // Aggiungo all'array
                URIs.add(urlOrEnabled)
            }
        }
    }

    @Throws(IOException::class)
    override fun onIncomingRequest(httpExchange: HttpExchange) {
        // Creo oggetto di ritorno
        val outputObject = JsonObject()
        outputObject.addProperty("status", "OK")
        outputObject.add("endpoints", URIs)

        // Invio i dati
        flushData(outputObject.toString())
    }
}