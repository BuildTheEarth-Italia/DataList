/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (DataList.java) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.bteitalia.datalist;

import it.bteitalia.datalist.server.RequestHandler;
import it.bteitalia.datalist.server.Server;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class DataList extends JavaPlugin {
    /**
     * Prefisso del plugin per messaggi in chat. Utilizza i {@link ChatColor color codes} di Bukkit.
     *
     * @see ChatColor
     */
    public static final String PREFIX = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[DataList] " + ChatColor.RESET;
    private static DataList instance;
    private static Float version;
    private Server server;

    /**
     * Usa questo metodo per ottenere l'istanza della classe {@link DataList}
     *
     * @return L'istanza corrente del plugin
     * @see JavaPlugin
     * @see Plugin
     */
    public static DataList getInstance() {
        return instance;
    }

    public Float getVersion() {
        return version;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        //fermo il server
        server.stop();

        //rimuovo l'instanza
        DataList.instance = null;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        //assegno l'istanza
        DataList.instance = this;

        //salvo i config di default
        saveDefaultConfig();
        updateConfigWithDefaults();
        
        // Ricarico le configurazioni per applicare le modifiche 
        reloadConfig();

        //salvo il certificato se non esiste e ssl abilitato
        if (!new File(getDataFolder(),
                getConfig().getString("ssl.name")).exists() &&
                getConfig().getBoolean("ssl.active")) {
            saveResource(getConfig().getString("ssl.name"), true);
        }

        //avvio il server con un runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //creo il server con la porta definita in output.port
                    //se non sicuro
                    if (getConfig().getBoolean("ssl.active")) {
                        server = Server.buildSecure(getConfig().getInt("output.port"), Executors.newCachedThreadPool());
                    } else {
                        server = Server.buildInsecure(getConfig().getInt("output.port"), Executors.newCachedThreadPool());
                    }

                    enableRoutes("output.path", getConfig()
                            .getConfigurationSection("output.path")
                            .getKeys(false));

                    //avvio il server
                    printInfo("Avvio il server HTTP");
                    server.run();
                } catch (IOException | YAMLException | GeneralSecurityException e) {
                    //fornisco una breve spiegazione se l'eccezione è un'instanza di YAMLException o di GeneralSecurityException
                    if (e instanceof YAMLException) {
                        getLogger().severe("Sembra che il file di configurazione non sia valido!");
                    } else if (e instanceof GeneralSecurityException) {
                        getLogger().severe("Sembra che il file di sicurezza non sia valido!");
                    }

                    //stampo l'errore
                    printError(e);

                    //fermo il plugin
                    printError("Disabilito il plugin");

                    if (server != null)
                        server.stop();
                    //   getServer().getPluginManager().disablePlugin(DataList.this);
                }
            }
        }.runTaskAsynchronously(this);

        String versionString = getServer().getClass().getPackage().getName();
        versionString = versionString.substring(versionString.lastIndexOf('.') + 1);

        version = Float.parseFloat(
                versionString.substring(1, versionString.lastIndexOf('_')).replace('_', '.')
        );
    }

    @SuppressWarnings("unchecked")
    private void enableRoutes(String path, Set<String> list) {
        list.forEach(obj -> {
            //percorso corrente
            String newPath = path + "." + obj;

            try {
                //cerco di ottenere le chiavi dell'oggetto, se fallisco vuol dire che è una chiave
                Set<String> objects = getConfig()
                        .getConfigurationSection(newPath)
                        .getKeys(false);

                //ripeto con nuovo percorso
                enableRoutes(newPath, objects);
            } catch (ClassCastException | NullPointerException ex) {
                //se non è abilitato ritorno
                Object urlOrEnabled = getConfig().get(newPath);

                // Controllo che sia una stringa
                if (!(urlOrEnabled instanceof String))
                    return;

                // Nome della classe handler
                String handlerName = newPath.replace("output.path.", "") + "RequestHandler";
                handlerName = Character.toUpperCase(handlerName.charAt(0)) + handlerName.substring(1);

                // Creo il percorso compreso di package
                String className = "it.bteitalia.datalist.handlers." + handlerName;

                try {
                    // Ottengo la classe
                    Class<? extends RequestHandler> handler = (Class<? extends RequestHandler>) Class.forName(className);

                    // Aggiungo l'handler
                    server.createContext((String) urlOrEnabled, handler.getConstructor().newInstance());

                } catch (ClassNotFoundException e) {
                    printError("Classe " + className + "non trovata", e);
                } catch (ClassCastException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    printError("Classe " + className + "non creabile", e);
                }
            }
        });
    }

    /**
     * Stampo l'errore in un box
     *
     * @param level     Livello di logging
     * @param reason    Motivo dell'errore
     * @param throwable Eccezione causa dell'errore
     * @see #printError(Throwable)
     * @see #printError(String, Throwable)
     * @see #printError(String)
     * @see #printInfo(String)
     */
    public void printError(Level level, String reason, Throwable throwable) {
        //stampo l'errore se reason != null
        if (reason != null)
            getLogger().log(level, reason);

        //stampo un blocco con lo stackTrace se throwable != null
        if (throwable != null) {
            getLogger().log(level, "========== Inizio Report ==========", throwable);
            getLogger().log(level, "==========  Fine Report  ==========");
        }
    }

    /**
     * Stampo l'errore in un box con livello {@link Level#SEVERE}
     *
     * @param reason    Motivo dell'errore
     * @param throwable Eccezione causa dell'errore
     * @see #printError(Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(String)
     * @see #printInfo(String)
     */
    public void printError(String reason, Throwable throwable) {
        printError(Level.SEVERE, reason, throwable);
    }

    /**
     * Stampo l'eccezione in un box con livello {@link Level#SEVERE}
     *
     * @param throwable Eccezione causa dell'errore
     * @see #printError(String, Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(String)
     * @see #printInfo(String)
     */
    public void printError(Throwable throwable) {
        printError(null, throwable);
    }

    /**
     * Stampo l'errore in un box con livello {@link Level#WARNING}
     *
     * @param reason Motivo dell'errore
     * @see #printError(String, Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(Throwable)
     * @see #printInfo(String)
     */
    public void printError(String reason) {
        printError(reason, null);
    }

    /**
     * Stampo un messaggio in un box con livello {@link Level#INFO}
     *
     * @param reason Messaggio da stampare
     * @see #printError(String, Throwable)
     * @see #printError(Level, String, Throwable)
     * @see #printError(Throwable)
     */
    public void printInfo(String reason) {
        printError(Level.INFO, reason, null);
    }

    private void updateConfigWithDefaults() {
        File currentConfigFile = new File(getDataFolder(), "config.yml");
        YamlConfiguration currentConfig = YamlConfiguration.loadConfiguration(currentConfigFile);

        for (String section : getConfig().getDefaults().getConfigurationSection("").getKeys(true))
            if (currentConfig.get(section) == null)
                currentConfig.set(section, getConfig().get(section));

        try {
            currentConfig.save(currentConfigFile);
        } catch (IOException e) {
            printError("Impossibile aggiornare le configurazioni", e);
        }
    }
}
