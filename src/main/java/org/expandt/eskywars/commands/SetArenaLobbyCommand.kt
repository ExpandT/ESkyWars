package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.expandt.eskywars.arena.ArenaManager

class SetArenaLobbyCommand(private val arenaManager: ArenaManager) {

    @Command(
        aliases = ["setarenalobby"],
        desc = "Set arena lobby",
        usage = "Используй: /setarenalobby <Название_Арены>"
    )
    fun setArenaLobbyCommand(player: CommandSender, arenaName: String) {
        arenaManager.setArenaLobby(arenaName, player as Player)
    }

}
