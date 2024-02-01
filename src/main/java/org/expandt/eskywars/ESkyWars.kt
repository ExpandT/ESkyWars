package org.expandt.eskywars

import app.ashcon.intake.bukkit.BukkitIntake
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.expandt.eskywars.arena.ArenaManager
import org.expandt.eskywars.commands.*
import org.expandt.eskywars.listeners.ArenaSetup
import org.expandt.eskywars.listeners.GuiListener
import org.expandt.eskywars.listeners.ProjectileLaunchListener
import org.expandt.eskywars.tabmanager.TabManager
import org.expandt.eskywars.trails.PlayerTrailsManager
import java.io.File
import java.io.IOException

class ESkyWars: JavaPlugin(), Listener {

    companion object {
        lateinit var INSTANCE: ESkyWars
    }

    private val arenaManager = ArenaManager(this)
    private val trailsManager = PlayerTrailsManager(this)
    private lateinit var tabManager: TabManager

    override fun onEnable() {
        INSTANCE = this
        logger.info("Enabling ESkyWars!")

        config.options().copyDefaults(true)
        saveConfig()

        createNeededFiles()
        registerCommands()
        registerEvents()
    }

    override fun onDisable() {
        logger.info("Disabling ESkyWars!")
    }

    @EventHandler
    fun onPluginEnable(event: PluginEnableEvent) {
        val plugin: Plugin = event.plugin
        if (plugin.name == "Multiverse-Core") {
            val multiverseCore = Bukkit.getPluginManager().getPlugin("Multiverse-Core")
            if (multiverseCore?.isEnabled!!) {
                arenaManager.loadArenas {}
                tabManager = TabManager(this, arenaManager)
            }
        }
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(this,this)
        server.pluginManager.registerEvents(ArenaSetup(arenaManager), this)
        server.pluginManager.registerEvents(ProjectileLaunchListener(trailsManager), this)
        server.pluginManager.registerEvents(GuiListener(), this)
    }

    private fun registerCommands() {
        val cmdGraph = BasicBukkitCommandGraph()
        cmdGraph.rootDispatcherNode.registerCommands(CreateArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(RemoveArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(RestartArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(JoinArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(LeaveArenaCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(SetArenaLobbyCommand(arenaManager))
        cmdGraph.rootDispatcherNode.registerCommands(CosmeticsCommand(trailsManager))
        cmdGraph.rootDispatcherNode.registerCommands(ParticleCommand())
        cmdGraph.rootDispatcherNode.registerCommands(SetMainLobbyCommand(arenaManager))
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
