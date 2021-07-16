/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (RequestHandler.java) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.bteitalia.datalist.DataList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public abstract class RequestHandler implements HttpHandler {
    private HttpExchange exchange;

    @Override
    public final void handle(HttpExchange httpExchange) throws IOException {
        // Se httpExchange == null esco
        if (httpExchange == null)
            return;

        // Salvo nella variabile locale una copia
        exchange = httpExchange;

        // Chiamo il metodo onIncomeRequest
        onIncomingRequest(exchange);

        // Creo log string
        StringBuilder toLog = new StringBuilder();
        String method = exchange.getRequestMethod();
        ChatColor methodColor = Methods.valueOf(method).color;

        // Aggiungo prefix colorato
        toLog.append(methodColor)
                .append("[").append(method).append("] ")
                .append(ChatColor.RESET);

        toLog.append("Richiesta ricevuta da ")
                .append(methodColor).append(ChatColor.ITALIC)
                .append(exchange.getRemoteAddress())
                .append(ChatColor.RESET)
                .append(", tramite il seguente URI ")
                .append(methodColor).append(ChatColor.ITALIC)
                .append(exchange.getRequestURI())
                .append(ChatColor.RESET).append('.');

        // Scrivo in console
        Bukkit.getServer().getConsoleSender().sendMessage(toLog.toString());
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

    private enum Methods {
        GET(ChatColor.AQUA),
        POST(ChatColor.GREEN),
        DELETE(ChatColor.RED),
        PATCH(ChatColor.YELLOW),
        PUT(ChatColor.GOLD);

        private final ChatColor color;

        Methods(ChatColor color) {
            this.color = color;
        }
    }
}
