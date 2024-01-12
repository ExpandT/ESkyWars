package org.expandt.eskywars.listeners

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.expandt.eskywars.arena.ArenaManager

object SelectedPoints {
    val selectedPoints =  mutableMapOf<Player, Pair<Location?, Location?>>()
}

class ArenaSetup(private val arenaManager: ArenaManager): Listener {

    private val selectedPoints = SelectedPoints.selectedPoints

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action == Action.RIGHT_CLICK_BLOCK && player.inventory.itemInMainHand.type == Material.DIAMOND_HOE) {
            event.isCancelled = true

            val clickedBlock = event.clickedBlock

            if (clickedBlock != null) {
                if (player.isSneaking) {
                    selectedPoints[player] = Pair(clickedBlock.location, null)
                    player.sendMessage("${ChatColor.GRAY}Первая Точка выбрана: ${ChatColor.GOLD}${clickedBlock.location}")
                } else {
                    val locations = selectedPoints[player]
                    if (locations != null && locations.second == null) {
                        selectedPoints[player] = Pair(locations.first, clickedBlock.location)
                        player.sendMessage("${ChatColor.GRAY}Вторая Точка выбрана: ${ChatColor.GOLD}${clickedBlock.location}")
                    } else {
                        player.sendMessage("${ChatColor.GRAY}Ты должен сначала выбрать первую точку. Используй Shift")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val arenaName = player.getMetadata("arena")[0].value() as String
        val arena = arenaManager.getArena(arenaName)

        event.keepInventory = true
        event.keepLevel = true
        event.drops.clear()


        arena?.onDeath(player)

        if (arena?.getArenaPlayers()?.size == 1) arenaManager.restartArena(arenaName)
    }
}
