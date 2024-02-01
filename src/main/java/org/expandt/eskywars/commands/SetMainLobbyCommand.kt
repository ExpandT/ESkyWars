package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.expandt.eskywars.arena.ArenaManager

class SetMainLobbyCommand(private val arenaManager: ArenaManager) {

    @Command(
        aliases = ["setmainlobby"],
        desc = "Sets main lobby",
        usage = "Используй: /setmainlobbt"
    )
    fun setMainLobby(player: CommandSender) {
        player.sendMessage("${ChatColor.GRAY}Главное лобби установлено")
        arenaManager.setMainLobby(player as Player)
    }

}
