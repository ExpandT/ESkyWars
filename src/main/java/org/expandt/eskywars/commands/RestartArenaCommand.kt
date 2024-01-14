package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.command.CommandSender
import org.expandt.eskywars.arena.ArenaManager

class RestartArenaCommand(private val arenaManager: ArenaManager) {

    @Command(
        aliases = ["restartarena"],
        desc = "Restarts Arena",
        usage = "Используй: /restartarena <Название_Арены>"
    )
    fun restartArenaCommand(player: CommandSender, arenaName: String) {
        arenaManager.restartArena(arenaName)
    }

}
