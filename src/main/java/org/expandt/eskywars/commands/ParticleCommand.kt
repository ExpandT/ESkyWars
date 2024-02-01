package org.expandt.eskywars.commands

import app.ashcon.intake.Command
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

class ParticleCommand {
    @Command(
        aliases = ["particle"],
        desc = "Open Cosmetic Menu",
        usage = "Используй: /particle"
    )
    fun spawnParticle(player: CommandSender) {
        val particleCount = 30
        val crownHeight = 2.0
        val p = player as Player

        for (i in 0 until particleCount) {
            val angle = 2 * Math.PI * i / particleCount
            val x = 1.5 * cos(angle)
            val z = 1.5 * sin(angle)

            val offsetX = x * 0.2
            val offsetZ = z * 0.2

            p.world.spawnParticle(
                Particle.REDSTONE,
                p.location.add(offsetX, crownHeight, offsetZ),
                1,
                0.0, 0.0, 0.0,
                Particle.DustOptions(Color.YELLOW, 1.0f)
            )
        }
    }

}
