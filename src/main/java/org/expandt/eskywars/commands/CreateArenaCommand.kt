package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.expandt.eskywars.arena.ArenaManager
import org.expandt.eskywars.listeners.SelectedPoints.selectedPoints


class CreateArenaCommand(private val arenaManager: ArenaManager) {

    @Command(
        aliases = ["createarena"],
        desc = "Create SkyWars Arena",
        usage = "Используй: /createarena <Название_Арены>"
    )
    fun createArenaCommand(player: CommandSender, arenaName: String) {
        val locations = selectedPoints[player]

        if( locations?.first == null || locations.second == null) {
            player.sendMessage("${ChatColor.GRAY}Сперва выбери две точки для Арены, используй для этого алмазную мотыгу")

            return
        }

        arenaManager.createArena(arenaName, locations.first, locations.second, player as Player)
    }

}
