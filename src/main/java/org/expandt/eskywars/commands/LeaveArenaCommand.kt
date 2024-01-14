package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.expandt.eskywars.arena.ArenaManager

class LeaveArenaCommand(private val arenaManager: ArenaManager) {

    @Command(
        aliases = ["leavearena"],
        desc = "Leave SkyWars arena",
        usage = "Используй: /leavearena <Название_Арены>"
    )
    fun leaveArenaCommand(player: CommandSender, arenaName: String) {
        arenaManager.leaveArena(arenaName, player as Player)
    }

}
