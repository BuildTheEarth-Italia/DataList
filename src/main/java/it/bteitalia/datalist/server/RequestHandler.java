/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (RequestHandler.java) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.bteitalia.datalist.DataList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public abstract class RequestHandler implements HttpHandler {
    private HttpExchange exchange;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        //se httpExchange == null esco
        if (httpExchange == null)
            return;

        //salvo in variabile
        exchange = httpExchange;

        //faccio il log
        log("Richiesta HTTP ricevuta da '" + exchange.getRemoteAddress() + "', per il percorso '" + exchange.getRequestURI() + "'");

        //chiamo il metodo onIncomeRequest
        onIncomingRequest(exchange);
    }

    protected abstract void onIncomingRequest(@NotNull HttpExchange httpExchange) throws IOException;

    protected void log(String reason, Throwable throwable) {
        DataList.getInstance().printError(reason, throwable);
    }

    protected void log(Throwable throwable) {
        DataList.getInstance().printError(throwable);
    }

    protected void log(String reason) {
        DataList.getInstance().printInfo(reason);
    }

    public final void flushData(@NotNull String data) throws IOException {
        //imposto gli header per consentire le richieste AJAX
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, data.getBytes().length);

        //apro l'outputstream
        OutputStream outputStream = exchange.getResponseBody();

        //scrivo
        outputStream.write(data.getBytes());

        //chiudo
        outputStream.close();
    }
}
