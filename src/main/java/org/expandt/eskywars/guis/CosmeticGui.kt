package org.expandt.eskywars.guis

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.expandt.eskywars.trails.PlayerTrailsManager

class CosmeticGui(private val trailsManager: PlayerTrailsManager): BasicGui(27, "${ChatColor.RED}Косметика") {


    override fun initialize() {

        val trailsItem: GuiItem = GuiItem.Builder()
            .stack(Material.BLAZE_POWDER)
            .displayName("${ChatColor.RED}Еффекты за стрелой")
            .onClick {
                val trailsGui = TrailsGui(trailsManager, it.whoClicked as Player)
                trailsGui.initialize()
                trailsGui.open(it.whoClicked as Player)
            }
            .build()

        val closeItem = GuiItem.Builder()
            .stack(Material.BARRIER)
            .onClick {  event ->
                close(event.whoClicked as Player)
            }
            .build()

        addItem(trailsItem, 10)
        addItem(closeItem, 1)
    }
}
