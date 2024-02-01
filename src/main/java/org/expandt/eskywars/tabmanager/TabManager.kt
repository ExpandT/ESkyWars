package org.expandt.eskywars.tabmanager

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import org.expandt.eskywars.arena.ArenaManager

class TabManager(private val plugin: JavaPlugin, private val arenaManager: ArenaManager) {
    private val arenaTeams = HashMap<String, Team>()

    init {
        createTeamsForArenas()
        startTabListUpdateTask()
    }

    private fun createTeamsForArenas() {
        for (arenaName in getArenaNames()) {
            val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(arenaName) ?: Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(arenaName)
            arenaTeams[arenaName] = team
        }
    }

    private fun startTabListUpdateTask() {
        object : BukkitRunnable() {
            override fun run() {
                updateTabListForAllPlayers()
            }
        }.runTaskTimer(plugin, 0, 20L)
    }

    private fun updateTabListForAllPlayers() {
        for (player in Bukkit.getOnlinePlayers()) {
            updateTabListForPlayer(player)
        }
    }

    private fun updateTabListForPlayer(player: Player) {
        val playerArena = getPlayerArena(player)

        for (otherPlayer in Bukkit.getOnlinePlayers()) {
            if (player != otherPlayer && getPlayerArena(otherPlayer) == playerArena) {
                player.showPlayer(plugin, otherPlayer)
            } else {
                player.hidePlayer(plugin, otherPlayer)
            }
        }
    }

    private fun getPlayerArena(player: Player): String {
        return arenaManager.getArenaName(player)
    }

    private fun getArenaNames(): List<String> {
        return arenaManager.getArenaList().map {
            it.key
        }
    }
}
