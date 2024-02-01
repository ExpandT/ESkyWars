package org.expandt.eskywars.listeners

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.scheduler.BukkitRunnable
import org.expandt.eskywars.ESkyWars
import org.expandt.eskywars.trails.PlayerTrailsManager

class ProjectileLaunchListener(private val trailsManager: PlayerTrailsManager): Listener {

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val entity = event.entity

        if (entity is Arrow && entity.shooter is Player) {
            val player = entity.shooter as Player

            object : BukkitRunnable() {
                override fun run() {
                    if(!entity.isDead && !entity.isOnGround) {
                        updateParticleLocation(player, entity)
                    } else {
                        cancel()
                    }
                }
            }.runTaskTimer(ESkyWars.INSTANCE, 0, 1)
        }
    }

    private fun updateParticleLocation(player: Player, arrow: Arrow) {
        val location: Location = arrow.location
        val particle = if (trailsManager.getSelectedTrail(player) == null) Particle.DRAGON_BREATH else trailsManager.getSelectedTrail(player)!!
        player.world.spawnParticle(
                particle,
                location,
                2,
                0.0, 0.0, 0.0,
                0.1
            )
    }

}
