package org.expandt.eskywars.utils

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type


class LocationAdapter : JsonSerializer<Location>, JsonDeserializer<Location> {
    override fun serialize(src: Location?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()

        if (src != null && src.world != null) {
            jsonObject.addProperty("world", src.world!!.name)
            jsonObject.addProperty("x", src.x)
            jsonObject.addProperty("y", src.y)
            jsonObject.addProperty("z", src.z)
            jsonObject.addProperty("yaw", src.yaw)
            jsonObject.addProperty("pitch", src.pitch)
        }

        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Location {
        val jsonObject = json.asJsonObject
        val world = Bukkit.getWorld(jsonObject["world"].asString)
        val x = jsonObject["x"].asDouble
        val y = jsonObject["y"].asDouble
        val z = jsonObject["z"].asDouble
        val yaw = jsonObject["yaw"].asFloat
        val pitch = jsonObject["pitch"].asFloat

        return Location(world, x, y, z, yaw, pitch)
    }
}

