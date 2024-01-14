package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.expandt.eskywars.arena.ArenaManager

class JoinArenaCommand(private val arenaManager: ArenaManager) {

    @Command(
        aliases = ["joinarena"],
        desc = "Join SkyWars arena",
        usage = "Используй: /joinarena <Название_Арены>"
    )
    fun joinArenaCommand(player: CommandSender, arenaName: String) {
        arenaManager.joinArena(arenaName, player as Player)
    }

}
