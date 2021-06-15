package it.bteitalia.datalist.listers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.scoreboard.Score

internal class PointsLister {
    fun getJSON(scoreboardName: String): JsonArray {
        //oggetto di out
        val out = JsonArray()

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val objective = scoreboard.getObjective(scoreboardName)

        for (entry in scoreboard.entries) {
            val score: Score? = objective.getScore(entry)

            // Se non esiste o è 0 ignoro
            if (score == null || score.score <= 0)
                continue

            // Creo oggetto
            val jsonPlayerEntry = JsonObject()
            jsonPlayerEntry.addProperty("name", entry)
            jsonPlayerEntry.addProperty("score", score.score)

            out.add(jsonPlayerEntry)
        }

        //ritorno l'array
        return out
    }
}