package org.expandt.eskywars

import app.ashcon.intake.bukkit.BukkitIntake
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph
import org.bukkit.plugin.java.JavaPlugin
import org.expandt.eskywars.arena.ArenaManager
import org.expandt.eskywars.commands.*
import org.expandt.eskywars.listeners.ArenaSetup
import java.io.File
import java.io.IOException

class ESkyWars: JavaPlugin() {

    companion object {
        lateinit var INSTANCE: ESkyWars
    }

    private val arenaManager = ArenaManager(this)
    override fun onEnable() {
        INSTANCE = this
        logger.info("Enabling ESkyWars!")

        config.options().copyDefaults(true)
        saveConfig()

        arenaManager.loadArenas()

        createNeededFiles()
        registerCommands()
        registerEvents()
    }

    override fun onDisable() {
        logger.info("Disabling ESkyWars!")
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(ArenaSetup(arenaManager), this)
    }

    private fun registerCommands() {
        val cmdGraph = BasicBukkitCommandGraph()
        cmdGraph.rootDispatcherNode.registerCommands(CreateArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(RemoveArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(RestartArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(JoinArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(LeaveArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(SetArenaLobbyCommand(arenaManager))
        BukkitIntake(this, cmdGraph).register()
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

}
