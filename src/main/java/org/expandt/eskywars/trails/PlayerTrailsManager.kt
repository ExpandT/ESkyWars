package org.expandt.eskywars.trails

import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlayerTrailsManager(private val plugin: JavaPlugin) {
    private val selectedTrails = mutableMapOf<String, Particle>()

    fun getSelectedTrail(player: Player): Particle? {
        return selectedTrails[player.uniqueId.toString()]
    }

    fun setSelectedTrail(player: Player, trailType: Particle) {
        selectedTrails[player.uniqueId.toString()] = trailType
    }
}
