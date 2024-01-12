package org.expandt.eskywars.utils

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import java.lang.reflect.Type

class BlockAdapter : JsonSerializer<Block>, JsonDeserializer<Block> {

    override fun serialize(src: Block?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val json = JsonObject()

        if (src != null) {
            val location = src.location
            val blockJson = JsonObject()
            blockJson.addProperty("world", location.world.name)
            blockJson.addProperty("x", location.blockX)
            blockJson.addProperty("y", location.blockY)
            blockJson.addProperty("z", location.blockZ)
            blockJson.addProperty("type", src.type.name)
            json.add("block", blockJson)
        }

        return json
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Block {
        val jsonObject = json?.asJsonObject
        val blockJson = jsonObject?.getAsJsonObject("block")

        if (blockJson != null) {
            val worldName = blockJson.get("world").asString
            val world: World? = Bukkit.getWorld(worldName)
            val x = blockJson.get("x").asInt
            val y = blockJson.get("y").asInt
            val z = blockJson.get("z").asInt
            val materialName = blockJson.get("type").asString

            if (world != null) {
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val block = location.block
                block.type = Material.matchMaterial(materialName) ?: Material.AIR

                return block
            }
        }

        throw JsonParseException("Invalid Block JSON")
    }
}
