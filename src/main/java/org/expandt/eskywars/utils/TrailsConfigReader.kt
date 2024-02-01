package org.expandt.eskywars.utils

import org.bukkit.Material
import org.bukkit.Particle
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream

data class Trail(
    val name: String,
    val material: Material,
    val particle: Particle
)

data class TrailsConfig(
    val trails: Map<String, Trail>
)
class TrailsConfigReader {
    fun readTrailsConfig(): TrailsConfig {
        val yaml = Yaml()
        val inputStream = FileInputStream(File("plugins/ESkyWars/config.yml"))
        val configMap = yaml.load(inputStream) as? Map<String, Map<String, Any>>

        val trailsMap = configMap?.get("trails") as? Map<String, Map<String, Any>>

        val trails = trailsMap?.mapValues { entry ->
            val trailMap = entry.value
            Trail(
                name = trailMap["name"].toString(),
                material = Material.getMaterial(trailMap["material"].toString())!!,
                particle = Particle.valueOf(trailMap["particle"].toString()),
            )
        } ?: emptyMap()

        return TrailsConfig(trails)
    }

}
