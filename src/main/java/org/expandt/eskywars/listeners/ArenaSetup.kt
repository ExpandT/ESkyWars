package org.expandt.eskywars.listeners

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.expandt.eskywars.ESkyWars
import org.expandt.eskywars.arena.ArenaManager
import org.expandt.eskywars.guis.KitSelectorGui


object SelectedPoints {
    val selectedPoints =  mutableMapOf<Player, Pair<Location?, Location?>>()
}

class ArenaSetup(private val arenaManager: ArenaManager): Listener {

    private val selectedPoints = SelectedPoints.selectedPoints

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if ((event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK ) && player.inventory.itemInMainHand.itemMeta.persistentDataContainer.has(
                NamespacedKey(ESkyWars.INSTANCE, "kit_selector"), PersistentDataType.STRING)) {
            val menu = KitSelectorGui()
            menu.initialize()
            menu.open(player)
            return
        }

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

        if (arena?.getArenaPlayers()?.size == 1) {

            var ctr = 6 // restarts arena after all players teleported to lobby

            object : BukkitRunnable() {
                override fun run() {
                    ctr--

                    if (ctr < 0) {

                        arenaManager.restartArena(arenaName)

                        cancel()
                    }
                }
            }.runTaskTimer(ESkyWars.INSTANCE, 0L, 20L)
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val arenaList = arenaManager.getArenaList()
        val player = event.player
        val message = event.message
        var isPlayerInArena = false

        for (arena in arenaList.values) {
            if (arena.getArenaPlayers().contains(player)) {
                isPlayerInArena = true

                for (arenaPlayer in arena.getArenaPlayers()) {
                    arenaPlayer.sendMessage("${player.displayName}: $message")
                }

                event.isCancelled = true

                break
            }
        }

        if (!isPlayerInArena) {
            event.recipients.removeIf { recipient ->
                for (arena in arenaList.values) {
                    if (arena.getArenaPlayers().contains(recipient)) {

                        return@removeIf true
                    }
                }
                false
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val arena = arenaManager.getArena(event.player)
        arena?.brokenBlocks?.add(event.block.state)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val arena = arenaManager.getArena(event.player)

        if (!arena?.isInsideArena(event.block.location)!!) {
            event.player.sendMessage("${ChatColor.RED}Вы не можете ставить блоки за пределами арены.")
            event.isCancelled = true
        }

        arena.placedBlocks.add(event.blockPlaced.state)
    }

}
