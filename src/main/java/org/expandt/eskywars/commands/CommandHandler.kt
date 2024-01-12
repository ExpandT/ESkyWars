package org.expandt.eskywars.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.expandt.eskywars.arena.ArenaManager
import org.expandt.eskywars.listeners.SelectedPoints

class CommandHandler(private val arenaManager: ArenaManager) {

    private val selectedPoints = SelectedPoints.selectedPoints

    fun handleCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        when {
            command.name.equals("createarena", ignoreCase = true) && sender is Player -> {
                handleCreateArenaCommand(sender, args)

                return true
            }
            command.name.equals("removearena", ignoreCase = true) && sender is Player -> {
                handleRemoveArenaCommand(sender, args)

                return true
            }
            command.name.equals("joinarena", ignoreCase = true) && sender is Player -> {
                handleJoinArenaCommand(sender, args)

                return true
            }
            command.name.equals("leavearena", ignoreCase = true) && sender is Player -> {
                handleLeaveArenaCommand(sender, args)

                return true
            }
            command.name.equals("setarenalobby", ignoreCase = true) && sender is Player -> {
                handleSetArenaLobbyCommand(sender, args)

                return true
            }
            command.name.equals("restartarena", ignoreCase = true) && sender is Player -> {
                handleRestartArenaCommand(sender, args)

                return true
            }
        }

        return false
    }

    private fun handleCreateArenaCommand(player: Player, args: Array<String>) {
        val locations = selectedPoints[player]
        val arenaName = args[0]

        if (args.size != 1) {
            player.sendMessage("${ChatColor.GRAY}Используй: /createarena <Название_Арены>")

            return
        }

        if( locations?.first == null || locations.second == null) {
            player.sendMessage("${ChatColor.GRAY}Сперва выбери две точки для Арены, используй для этого алмазную мотыгу")

            return
        }

        arenaManager.createArena(arenaName, locations.first, locations.second, player)
    }

    private fun handleRemoveArenaCommand(player: Player, args: Array<String>) {
        if (args.size != 1) {
            player.sendMessage("Используй: /removearena <Название_Арены>")

            return
        }

        val arenaName = args[0]
        arenaManager.removeArena(arenaName, player)
        player.sendMessage("${ChatColor.GRAY}Арена '${ChatColor.RED} $arenaName' удалена!")
    }

    private fun handleJoinArenaCommand(player: Player, args: Array<String>) {
        if (args.size != 1) {
            player.sendMessage("${ChatColor.GRAY}Используй: /joinarena <Название_Арены>")

            return
        }

        val arenaName = args[0]
        arenaManager.joinArena(arenaName, player)
    }

    private fun handleLeaveArenaCommand(player: Player, args: Array<String>) {
        if (args.size != 1) {
            player.sendMessage("${ChatColor.GRAY}Используй: /leavearena <Название_Арены>")

            return
        }

        val arenaName = args[0]
        arenaManager.leaveArena(arenaName, player)
    }

    private fun handleSetArenaLobbyCommand(player: Player, args: Array<String>) {
        if (args.size != 1) {
            player.sendMessage("${ChatColor.GRAY}Используй: /setarenalobby <Название_Арены>")

            return
        }

        val arenaName = args[0]
        arenaManager.setArenaLobby(arenaName, player.location, player)
    }

    private fun handleRestartArenaCommand(player: Player, args: Array<String>) {
        if (args.size != 1) {
            player.sendMessage("${ChatColor.GRAY}Используй: /restartarena <Название_Арены>")

            return
        }

        val arenaName = args[0]
        arenaManager.restartArena(arenaName)
    }

}
