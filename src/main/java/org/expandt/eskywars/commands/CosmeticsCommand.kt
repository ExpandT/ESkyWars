package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.expandt.eskywars.guis.CosmeticGui
import org.expandt.eskywars.trails.PlayerTrailsManager

class CosmeticsCommand(private val trailsManager: PlayerTrailsManager) {

    @Command(
        aliases = ["cosmetics"],
        desc = "Open Cosmetic Menu",
        usage = "Используй: /cosmetics"
    )
    fun openCosmeticMenu(player: CommandSender) {
        val menu = CosmeticGui(trailsManager)
        menu.initialize()
        menu.open(player as Player)
    }
}
