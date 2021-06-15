# BanList
## Cos'è questo BanList?
BanList è un semplicissimo plugin per _Spigot/Bukkit_ che ti permette di avere in formato JSON la lista di utenti bannati nel tuo server Minecraft, sia per indirizzo IP che per username!
## Storia di BanList
Un giorno, non sapendo cosa fare a causa del coronavirus 🦠, ho perso quaranta minuti della mia vita a realizzare il plugin...
## Come si installa?
### Requisiti
Il plugin [Essentials](https://github.com/EssentialsX/Essentials) è necessario per visualizzare gli utenti mutati, ma ritengo che questo ottimo plugin tu lo abbia già installato!
Inoltre, se vuoi vedere la lista di utenti congelati, installa il mio plugin [Freezer](https://github.com/rospino74/Freezer). Non ti basta ancora? Con [Vault](https://github.com/MilkBowl/Vault) installato potrai addirittura ottenere i gruppi di utenti e i loro bilanci!
### Utenti base
* Scarica l'[ultima relase](https://github.com/rospino74/BanList/releases/latest)
* Sposta il file `BanList-<version>.jar` nella cartella `plugins` del tuo server Minecraft
* Riavvia il server e all'indirizzo `http://<tuo-dominio-o-ip>:80/ban` troverai la lista di utenti bannati
### Utenti Avanzati
* Clona il repository e importalo nel tuo IDE
* Compila i files Java e Kotlin (è necessario che il tuo IDE sia configurato per quest'ultimo)
* Chiudi tutto in un Jar e dallo da mangiare al tuo server 😋!
## Configurazione
La configurazione di default del plugin è questa:
```yaml
ssl:
  active: true
  password: "banlist"
  name: "key.jks"

# Percorsi in cui mostrare l'output
output:
  path:
    ban: false
    onlinePlayers: "/online"
    permissions: "/permissions"
    points: "/points"
  port: 80
```
### SSL
Puoi decidere se usare un certificato SSL per garantire la sicurezza del tuo sito. Il servizio SSL è attivato di default con una chiave autofirmata presente nel JAR.

#### Creare una chiave autofirmata
Per creare un keystore autofirmato puoi usare il seguente comando
```bash
keytool -genkeypair \
        -keyalg RSA \
        -alias selfsigned \
        -keystore <name> \
        -storepass <password> \
        -validity 360 \
        -keysize 2048
```

Dove:
* `name` è il nome del keystore, da scrivere in `config.yml`
* `password` è la password che protegge il keystore, da scrivere in `config.yml`

#### Importare un certificato SSL in un keystore
https://ordina-jworks.github.io/security/2019/08/14/Using-Lets-Encrypt-Certificates-In-Java.html#using-the-certificates-in-a-java-application

### Output
#### `path`
Ognuna delle chiavi sottostanti a `path` può avere solo due valori: `false` oppure essere una stringa.
Sel è `false` allora il percorso sarà disabilitato, altrimenti verrà utilizzato il percorso scelto.
#### `port`
Deve essere un intero compreso tra `0` e `65565`, il numero scelto inoltre non deve corrispondere a nessuno porta già in uso, vedi [gli errori comuni](#errori-comuni).

## Output di esempio
### Ban
```json
{
   "byNAME": [
      {
         "name":"MemoryOfLife",
         "until":1585564044000,
         "forever":false,
         "created":1585564034000,
         "admin":"MemoryOfLife",
         "reason":"Sparisci dal mio server, Canaglia!"
      }
   ],
   "byIP": [
    {
         "name":"127.0.0.1",
         "until":0,
         "forever":true,
         "created":1585564034000,
         "admin":"MemoryOfLife",
         "reason":"Sparisci dal mio server, Canaglia!"
      }
   ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del player bannato o il suo indirizzo IP |
| `until` | `int` | Data del termine del ban. È una data formato Unix |
| `forever` | `bool` | Se è `true` il ban è permanente |
| `created` | `int` | Data di creazione del ban. È una data formato Unix |
| `admin` | `String` | Nome del admin che ha effetuato il ban. Può essere il nome di un player o `Server` se il ban è eseguito dalla console |
| `reason` | `String` | Motivo del ban |
### Scoreboard
```json
{
  "ScoreboadName": [
    {
      "name": "MemoryOfLife",
      "score": 500
    }
  ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del player |
| `score` | `int` | Punti del player in quel determinato scoreboard |
### Permissions
```json
{
  "groups": [
    {
      "name": "default",
      "members": [
        "MemoryOfLife"
      ]
    },
    {
      "name": "admin",
      "members": [
        "MemoryOfLife"
      ]
    }
  ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del gruppo |
| `members` | `Array` di `String` | Membri del gruppo |

## Errori comuni
* `java.net.BindException`: La porta scelta è già in uso, cambiarla nel file di configurazione
* `java.io.IOException`: Si è verificato un errore nel comunicare con un altro sistema
* `org.yaml.snakeyaml.error.YAMLException`: Si è verificato un errore nel leggere il file di configurazione, verifica se è valido!
