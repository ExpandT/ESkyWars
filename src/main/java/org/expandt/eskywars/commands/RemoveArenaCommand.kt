package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.expandt.eskywars.arena.ArenaManager

class RemoveArenaCommand(private val arenaManager: ArenaManager) {

    @Command(
        aliases = ["removearena"],
        desc = "Remove Arena",
        usage = "Используй: /removearena <Название_Арены>"
    )
    fun removeArenaCommand(player: CommandSender, arenaName: String) {
        arenaManager.removeArena(arenaName, player as Player)
        player.sendMessage("${ChatColor.GRAY}Арена '${ChatColor.RED} $arenaName' удалена!")
    }

}
