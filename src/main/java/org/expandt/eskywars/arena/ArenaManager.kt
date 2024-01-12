package org.expandt.eskywars.arena

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.expandt.eskywars.utils.BlockAdapter
import org.expandt.eskywars.utils.LocationAdapter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ArenaManager(private val plugin: JavaPlugin) {

    private val arenaList: MutableMap<String, Arena> = mutableMapOf()
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Location::class.java, LocationAdapter())
        .registerTypeAdapter(Block::class.java, BlockAdapter())
        .excludeFieldsWithoutExposeAnnotation()
        .setPrettyPrinting()
        .create()
    private val arenasFile: File = File(plugin.dataFolder, "arenas.json")
    private val arenaListType = object : TypeToken<MutableMap<String, Arena>>() {}.type

    fun createArena(name: String, corner1: Location?, corner2: Location?, player: Player) {
        if (arenaList.containsKey(name)) {
            player.sendMessage("${ChatColor.RED}Арена с таким именем уже существует!")
            return
        }

        val arena = Arena(name, corner1, corner2, lobby = null)
        arenaList[name] = arena

        player.sendMessage("${ChatColor.GRAY}Арена ${ChatColor.RED} $name создана!")
        saveArenas()
    }

    fun getArena(name: String): Arena? {
        return arenaList[name]
    }

    fun joinArena(name: String, player: Player) {
        arenaList[name]?.joinArena(player)
    }

    fun leaveArena(name: String, player: Player) {
        arenaList[name]?.leaveArena(player)
    }

    fun setArenaLobby(name: String, location: Location, player: Player) {
        arenaList[name]?.lobby = location
        saveArenas()

        player.sendMessage("${ChatColor.GRAY}Лобби для Арены -${ChatColor.RED} $name ${ChatColor.GRAY}установлено!")
    }

    fun loadArenas() {
            FileReader(arenasFile).use { reader ->
                val arenasData: MutableMap<String, Arena>? = gson.fromJson(reader, arenaListType)

                if (arenasData != null) {
                    arenaList.clear()
                    arenaList.putAll(arenasData)
                } else {
                    plugin.logger.warning("Файл arenas.json пуст или имеет недопустимый формат.")
                }
            }
    }

    fun saveArenas() {
        FileWriter(arenasFile).use { writer ->
            gson.toJson(arenaList, arenaListType, writer)
        }
    }

    fun restartArena(name: String) {
        loadArenas()
        arenaList[name]?.restartArena()
    }

    fun removeArena(name: String, player: Player) {
        if (arenaList.containsKey(name)) {
            arenaList.remove(name)
            saveArenas()
        } else {
            player.sendMessage("${ChatColor.RED}Арены с именем '$name' не существует!")
        }
    }


}
