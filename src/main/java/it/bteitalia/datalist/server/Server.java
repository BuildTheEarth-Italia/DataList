/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (Server.java) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import it.bteitalia.datalist.DataList;
import org.bukkit.configuration.file.FileConfiguration;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.Executor;

public class Server {
    private final HttpServer server;

    public Server(HttpsServer server) {
        this.server = server;
    }
    public Server(HttpServer server) {
        this.server = server;
    }

    /**
     * Crea un'instanza del server HTTPS, per crearne uno non sicuro usa {@link #buildInsecure(int, Executor)}
     *
     * @param port     Porta da utilizzare per la creazione del server
     * @param executor Processo sul quale eseguire il server
     * @throws IOException              Lancia una {@link IOException} se si verifica un errore nella creazione del server
     * @throws GeneralSecurityException Lancia una {@link GeneralSecurityException} se si verifica un errore nelle chiavi di sicurezza
     * @see #stop(int)
     * @see #stop()
     */
    public static Server buildSecure(int port, Executor executor) throws IOException, GeneralSecurityException {
        //prendo il file di configurazione
        FileConfiguration config = DataList.getInstance().getConfig();

        HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(executor);

        SSLContext sslContext = SSLContext.getInstance("TLS");

        // prendo password da file di configurazione
        char[] password = config.getString("ssl.password").toCharArray();
        // prendo file dalla configurazione
        FileInputStream fis = new FileInputStream(
                new File(DataList.getInstance().getDataFolder(),
                        config.getString("ssl.name")));

        //creo il keystore
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(fis, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                // initialise the SSL context
                SSLContext context = getSSLContext();
                SSLEngine engine = context.createSSLEngine();
                params.setNeedClientAuth(false);
                params.setCipherSuites(engine.getEnabledCipherSuites());
                params.setProtocols(engine.getEnabledProtocols());

                // Set the SSL parameters
                SSLParameters sslParameters = context.getSupportedSSLParameters();
                params.setSSLParameters(sslParameters);
            }
        });


        //ritorno il server
        return new Server(server);
    }

    /**
     * Crea un'instanza del server HTTP, per crearne uno non sicuro usa {@link #buildSecure(int, Executor)}
     *
     * @param port     Porta da utilizzare per la creazione del server
     * @param executor Processo sul quale eseguire il server
     * @throws IOException              Lancia una {@link IOException} se si verifica un errore nella creazione del server
     * @see #stop(int)
     * @see #stop()
     */
    public static Server buildInsecure(int port, Executor executor) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(executor);

        //ritorno il server
        return new Server(server);
    }

    /**
     * Ferma il server creato con {@link #buildSecure(int, Executor)} o {@link #buildInsecure(int, Executor)}
     *
     * @param errCode messaggio di errore
     */
    public void stop(int errCode) {
        //se il server non esiste ritorno
        if (server == null)
            return;

        //fermo il server
        server.stop(errCode);
    }

    /**
     * Ferma il server creato con {@link #buildSecure(int, Executor)} o {@link #buildInsecure(int, Executor)}, il codice di errore di default è 0
     */
    public void stop() {
        stop(0);
    }

    /**
     * Crea un contesto per il server
     *
     * @param path           Percorso del contesto
     * @param requestHandler {@link RequestHandler} per il contesto
     */
    public void createContext(String path, RequestHandler requestHandler) {
        server.createContext(path, requestHandler);
    }

    /**
     * Rimuove un contesto per il server
     *
     * @param path           Percorso del contesto
     */
    public void removeContext(String path) {
        server.removeContext(path);
    }

    public void run() {
        server.start();
    }
}
