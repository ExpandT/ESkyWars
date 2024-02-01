package org.expandt.eskywars.guis

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.expandt.eskywars.trails.PlayerTrailsManager
import org.expandt.eskywars.utils.TrailsConfigReader

class TrailsGui(private val trailsManager: PlayerTrailsManager, private val opener: Player): BasicGui(36, "${ChatColor.RED}Еффекты за стрелой") {
    override fun initialize() {
        var slotIndex = 10

        val trailsConfig = TrailsConfigReader().readTrailsConfig().trails.map { (_, trail) ->
            val displayName = if (isSelectedTrail(trail.particle, opener)) "${ChatColor.GREEN}Выбрано - ${ChatColor.RED}${trail.name}" else "${ChatColor.RED}${trail.name}"

            GuiItem.Builder()
                .stack(trail.material)
                .displayName(displayName)
                .onClick {
                    val player = it.whoClicked as Player
                    trailsManager.setSelectedTrail(player, trail.particle)
                    player.sendMessage("${ChatColor.GREEN}Выбран эффект: ${trail.name}")
                    close(player)
                }
                .build()
        }


        trailsConfig.forEach {
            addItem(it, slotIndex)
            slotIndex++
        }
    }

    private fun isSelectedTrail(trailName: Particle, player: Player): Boolean {
        val selectedTrail = trailsManager.getSelectedTrail(player)
        return selectedTrail != null && selectedTrail == trailName
    }
}
