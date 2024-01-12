package org.expandt.eskywars

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.expandt.eskywars.arena.ArenaManager
import org.expandt.eskywars.commands.CommandHandler
import org.expandt.eskywars.listeners.ArenaSetup
import java.io.File
import java.io.IOException

class ESkyWars: JavaPlugin() {

    companion object {
        lateinit var INSTANCE: ESkyWars
    }

    private val arenaManager = ArenaManager(this)
    private val commandHandler = CommandHandler(arenaManager)

    override fun onEnable() {
        INSTANCE = this
        logger.info("Enabling ESkyWars!")

        arenaManager.loadArenas()

        if (!config.contains("chest_items")) {
            createDefaultChestItemsConfig()
        }

        createNeededFiles()
        registerEvents()
    }

    override fun onDisable() {
        logger.info("Disabling ESkyWars!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        return commandHandler.handleCommand(sender, command, label, args)
    }


    private fun registerEvents() {
        server.pluginManager.registerEvents(ArenaSetup(arenaManager), this)
    }

    private fun createNeededFiles() {
        val arenasFile = File(dataFolder, "arenas.json")

        try {
            if (!arenasFile.exists()) {
                arenasFile.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun createDefaultChestItemsConfig() {
        val configSection = config.createSection("chest_items")

        configSection.set("weapons", mapOf("DIAMOND_SWORD" to 1, "IRON_SWORD" to 1, "BOW" to 1))
        configSection.set("blocks", mapOf("DIAMOND_BLOCK" to 16, "IRON_BLOCK" to 16, "BOOKSHELF" to 16))
        configSection.set("food", mapOf("APPLE" to 16, "BREAD" to 16, "COOKED_BEEF" to 16))
        configSection.set("other", mapOf("DIAMOND" to 8, "GOLD_INGOT" to 8, "REDSTONE" to 8))

        try {
            saveConfig()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
