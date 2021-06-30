# DataList
## Cos'è questo DataList?
Questo plugin è un [fork](https://github.com/rospino74/BanList) sviluppato apposta per il progetto _Build The Earth_ italiano come integrazione con il [sito web](https://github.com/BuildTheEarth-Italia/Wordpress-Theme).
## Come si installa?
### Requisiti
Il plugin [Vault](https://github.com/milkbowl/Vault) è necessario per visualizzare i permessi, ma ritengo che questo ottimo plugin tu lo abbia già installato!
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
    playtime: "/playtime"
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
### Playtime
```json
{
  "playtime": [
    {
      "name": "MemoryOfLife",
      "ticks": 2281
    }
  ]
}
```
| Chiave | Tipo | Significato |
| :--- | :---: | --- |
| `name` | `String` | Nome del player |
| `ticks` | `int` | Numero di ticks in cui il player è stato online |

⚠ Se il **server non ha attivato** il le **statistiche** **non** sarà possibile **visualizzarle**. Per info visita [Spigot Configuration page](https://www.spigotmc.org/wiki/spigot-configuration/) alla voce _Stats_.

## Errori comuni
* `java.net.BindException`: La porta scelta è già in uso, cambiarla nel file di configurazione
* `java.io.IOException`: Si è verificato un errore nel comunicare con un altro sistema
* `org.yaml.snakeyaml.error.YAMLException`: Si è verificato un errore nel leggere il file di configurazione, verifica se è valido!
* `java.lang.ClassNotFoundException` oppure `java.lang.InstantiationException`: La classe che hai specificato in `config.yml` non è valida. [Vedi sotto](#nota-per-i-developer). 
* `java.lang.NoSuchMethodException` oppure `java.lang.ClassCastException`: La classe che hai specificato in `config.yml` non estende la classe [RequestHandler](https://github.com/BuildTheEarth-Italia/DataList/blob/master/src/main/java/it/bteitalia/datalist/server/RequestHandler.java).

## Nota per i Developer
Ogni child di `output.path` nella [configurazione](#configurazione) ha un nome speciale.
Il plugin a ogni startup carica la classe `it.bteitalia.datalist.handlers.<path-name>RequestHandler` come handler per il percorso specificato in `output.path.<path-name>`.
Per cui se sbagliate il nome in `config.yml` il plugin genererà un eccezione [`java.lang.ClassNotFoundException`](#errori-comuni)