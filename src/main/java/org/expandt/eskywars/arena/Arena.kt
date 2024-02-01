package org.expandt.eskywars.arena

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore
import com.comphenix.protocol.wrappers.EnumWrappers
import com.google.gson.annotations.Expose
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.block.Chest
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import org.expandt.eskywars.ESkyWars
import org.expandt.eskywars.kit.KitManager
import java.io.File


class Arena (
    @Expose val name: String,
    @Expose val corner1: Location?,
    @Expose val corner2: Location?,
    @Expose val timeToStart: Int = 5,
    @Expose val minPlayersToStart: Int = 2,
    @Expose var lobby: Location?,
    @Expose var placedBlocks: ArrayList<BlockState> = ArrayList(),
    @Expose var brokenBlocks: ArrayList<BlockState> = ArrayList(),
    @Expose private val players: MutableList<Player> = ArrayList(),
    @Expose private val spectators: MutableList<Player> = ArrayList(),
    @Expose private var emeraldBlocks: MutableList<Location> = mutableListOf(),
    @Expose private var chestList: MutableList<Location> = mutableListOf(),
) {

    init {
        validateCorners()
        populateBlocksBetweenCorners()
        chestList = findBlocksInCube(corner1!!, corner2!!, Material.CHEST)
        emeraldBlocks = findBlocksInCube(corner1, corner2, Material.EMERALD_BLOCK)
    }

    fun joinArena(player: Player) {
        players.add(player)
        player.setMetadata("arena", FixedMetadataValue(ESkyWars.INSTANCE, name))

        if (lobby != null) {
            player.teleport(lobby!!)
            sendArenaMessage("${ChatColor.RED}${player.name} ${ChatColor.GRAY}присоединился")

            if (players.size >= minPlayersToStart) startTimer()
        } else {
            player.sendMessage("${ChatColor.GRAY}Ошибка: Лобби не установлено для арены.")
        }
    }

    fun leaveArena(player: Player) {
        player.removeMetadata("kit", ESkyWars.INSTANCE)
        players.remove(player)
        sendArenaMessage("${ChatColor.RED}${player.name} ${ChatColor.GRAY} вышел!")
        player.scoreboard.clearSlot(DisplaySlot.SIDEBAR)
    }

    fun sendArenaMessage(msg: String) {
        for (player in players) {
            player.sendMessage(msg)
        }
    }

    fun sendArenaTitle(msg: String, subMsg: String) {
        for (player in players) {
            player.sendTitle(msg, subMsg, 10, 40, 10);
        }
    }

    fun playArenaSound(sound: Sound) {
        for (player in players) {
            player.playSound(player.location, sound, 1.toFloat(), 1.toFloat() );
        }
    }

    fun teleportPlayers() {
        for ((index, player) in players.withIndex()) {
            val teleportLocation = emeraldBlocks[index].clone().add(0.5, 1.5, 0.5)
            val kitSelectorItem = ItemStack(Material.BOW)
            val kitSelectorMeta = kitSelectorItem.itemMeta
            kitSelectorMeta.persistentDataContainer.set(NamespacedKey(ESkyWars.INSTANCE, "kit_selector"), PersistentDataType.STRING, "selector")
            kitSelectorMeta.setDisplayName("${ChatColor.RED}Выбор класса")

            kitSelectorItem.itemMeta = kitSelectorMeta

            player.inventory.addItem(kitSelectorItem)
            player.health = 20.0
            player.foodLevel = 20
            player.teleport(teleportLocation)
        }
    }

    fun startPreparation() {
        createGlassCageAroundEmerald()
        changePlayersGamemode(GameMode.ADVENTURE)
        generateChestLoot()
        teleportPlayers()

        object : BukkitRunnable() {
            override fun run() {
                setGameScoreboard()
            }
        }.runTaskTimer(ESkyWars.INSTANCE, 0, 20L)

        var ctr = timeToStart

        object : BukkitRunnable() {
            override fun run() {
                sendArenaTitle("${ChatColor.GRAY}До начала осталось - $ctr!", "Приготовься!")
                playArenaSound(Sound.BLOCK_NOTE_BLOCK_PLING)

                ctr--

                if (players.size < minPlayersToStart) {
                    sendArenaMessage("Недостаточно игроков!")
                    playArenaSound(Sound.ENTITY_VILLAGER_NO)
                    cancel()
                }

                if (ctr < 0) {
                    cancel()

                    startGame()
                }
            }
        }.runTaskTimer(ESkyWars.INSTANCE, 0L, 20L)

    }

    fun startGame() {
        removeCages()
        applyPlayerKits()
        changePlayersGamemode(GameMode.SURVIVAL)
        sendArenaTitle("${ChatColor.GRAY} Игра началась!", "${ChatColor.RED}Сражайтесь с другими игрокми!")
    }

    fun getArenaPlayers(): List<Player> {
        return players
    }

    fun onDeath(player: Player) {
        player.gameMode = GameMode.SPECTATOR
        sendArenaMessage("${ChatColor.GRAY}Игрок ${ChatColor.RED}${player.name} умер!")
        players.remove(player)
        spectators.add(player)

        if (players.size == 1) {
            endGame()
        }
    }

    fun restartArena() {
        for (state in brokenBlocks) {
            state.update(true, false)
        }

        for (state in placedBlocks) {

            state.block.type = Material.AIR
        }

        for (emeraldBlock in emeraldBlocks) {
            emeraldBlock.block.type = Material.EMERALD_BLOCK
        }

        brokenBlocks.clear()
        placedBlocks.clear()
    }

    fun generateChestLoot() {
        val chestItemGenerator = ChestItemGenerator()

        for (location in chestList) {
            val chest = location.block.state as Chest
            val items = chestItemGenerator.generateItems()
            chest.blockInventory.clear()

            for (item in items) {
                chest.blockInventory.addItem(item)
            }
        }
    }

    fun isInsideArena(location: Location): Boolean {
        val minX = Math.min(corner1?.blockX!!, corner2?.blockX!!)
        val minY = Math.min(corner1.blockY, corner2.blockY)
        val minZ = Math.min(corner1.blockZ, corner2.blockZ)
        val maxX = Math.max(corner1.blockX, corner2.blockX)
        val maxY = Math.max(corner1.blockY, corner2.blockY)
        val maxZ = Math.max(corner1.blockZ, corner2.blockZ)

        return (location.blockX in minX..maxX) &&
                (location.blockY in minY..maxY) &&
                (location.blockZ in minZ..maxZ)
    }

    private fun applyPlayerKits() {
        val kitManager = KitManager()

        players.forEach {
            it.inventory.clear()

            if(it.hasMetadata("kit")) {
                val kitName = it.getMetadata("kit")[0].value() as String

                kitManager.applyKit(it, kitName)
            }
        }
    }

    private fun endGame() {
        val configFile = File(ESkyWars.INSTANCE.dataFolder, "config.yml")
        val config = YamlConfiguration.loadConfiguration(configFile)
        val mainLobbyLocation = config.get("main-lobby") as Location

        val winner = players.first()

        winner.gameMode = GameMode.SURVIVAL
        winner.health = 20.0
        winner.foodLevel = 20
        sendArenaMessage("${ChatColor.GRAY}Игрок ${ChatColor.RED}${winner.name} победил!")


        var ctr = 5

        object : BukkitRunnable() {
            override fun run() {
                ctr--

                if (ctr < 0) {

                    winner.scoreboard.clearSlot(DisplaySlot.SIDEBAR)
                    winner.teleport(mainLobbyLocation)
                    winner.removeMetadata("kit", ESkyWars.INSTANCE)

                    for (player in spectators) {
                        player.removeMetadata("kit", ESkyWars.INSTANCE)
                        player.gameMode = GameMode.SURVIVAL
                        player.health = 20.0
                        player.foodLevel = 20
                        player.scoreboard.clearSlot(DisplaySlot.SIDEBAR)
                        player.teleport(mainLobbyLocation)
                    }

                    spectators.clear()
                    players.clear()

                    cancel()
                }
            }
        }.runTaskTimer(ESkyWars.INSTANCE, 0L, 20L)

    }

    private fun setGameScoreboard() {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective = scoreboard.registerNewObjective("Sw", "dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR
        objective.displayName = "${ChatColor.RED}${ChatColor.BOLD}ESkyWars"

        val lines = mutableListOf(
            "",
            "${ChatColor.GRAY}Игроков: ${ChatColor.RED}${players.size}/${emeraldBlocks.size}",
            "",
            "${ChatColor.GRAY}Арена - ${ChatColor.RED}$name",
            ""
        )

        for (player in players) {
            if (player.scoreboard != scoreboard) {
                player.scoreboard = scoreboard
            }

            for ((index, line) in lines.withIndex()) {
                sendScoreBoardPacket(player, line, index)
            }

        }
    }

    private fun sendScoreBoardPacket(player: Player, line: String, index: Int) {
        val packet = WrapperPlayServerScoreboardScore()
        packet.objectiveName = "Sw"
        packet.setScoreboardAction(EnumWrappers.ScoreboardAction.CHANGE)
        packet.value = index
        packet.scoreName = line

        packet.sendPacket(player)
    }
    private fun startTimer() {
        var ctr = timeToStart

        object : BukkitRunnable() {
            override fun run() {
                sendArenaTitle("До начала осталось - $ctr!", "Приготовься!")
                playArenaSound(Sound.BLOCK_NOTE_BLOCK_PLING)

                ctr--

                if (players.size < minPlayersToStart) {
                    sendArenaMessage("Недостаточно игроков!")
                    playArenaSound(Sound.ENTITY_VILLAGER_NO)
                    cancel()
                }

                if (ctr < 0) {
                    cancel()

                    startPreparation()
                }
            }
        }.runTaskTimer(ESkyWars.INSTANCE, 0L, 20L)
    }

    private fun changePlayersGamemode(gameMode: GameMode) {
        for (player in players) {
            player.gameMode = gameMode
        }
    }

    private fun removeCages() {
        for (emeraldBlockLocation in emeraldBlocks) {
            val emeraldBlock = emeraldBlockLocation.block
            emeraldBlock.type = Material.AIR

            for (i in 1..3) {
                emeraldBlock.getRelative(BlockFace.EAST, 1).type = Material.AIR
                emeraldBlock.getRelative(BlockFace.WEST, 1).type = Material.AIR
                emeraldBlock.getRelative(BlockFace.NORTH, 1).type = Material.AIR
                emeraldBlock.getRelative(BlockFace.SOUTH, 1).type = Material.AIR

                emeraldBlock.getRelative(BlockFace.EAST, 1).getRelative(BlockFace.UP, i).type = Material.AIR
                emeraldBlock.getRelative(BlockFace.WEST, 1).getRelative(BlockFace.UP, i).type = Material.AIR
                emeraldBlock.getRelative(BlockFace.NORTH, 1).getRelative(BlockFace.UP, i).type = Material.AIR
                emeraldBlock.getRelative(BlockFace.SOUTH, 1).getRelative(BlockFace.UP, i).type = Material.AIR

            }
        }
    }

    private fun createGlassCageAroundEmerald() {
        for (emeraldBlockLocation in emeraldBlocks) {
            createGlassCage(emeraldBlockLocation)
        }
    }

    private fun createGlassCage(centerLocation: Location) {
        val emeraldBlock = centerLocation.block

        if (emeraldBlock.type != Material.EMERALD_BLOCK) {
            return
        }

        emeraldBlock.type = Material.GLASS

        for (i in 1..3) {
            emeraldBlock.getRelative(BlockFace.EAST, 1).type = Material.GLASS
            emeraldBlock.getRelative(BlockFace.WEST, 1).type = Material.GLASS
            emeraldBlock.getRelative(BlockFace.NORTH, 1).type = Material.GLASS
            emeraldBlock.getRelative(BlockFace.SOUTH, 1).type = Material.GLASS

            emeraldBlock.getRelative(BlockFace.EAST, 1).getRelative(BlockFace.UP, i).type = Material.GLASS
            emeraldBlock.getRelative(BlockFace.WEST, 1).getRelative(BlockFace.UP, i).type = Material.GLASS
            emeraldBlock.getRelative(BlockFace.NORTH, 1).getRelative(BlockFace.UP, i).type = Material.GLASS
            emeraldBlock.getRelative(BlockFace.SOUTH, 1).getRelative(BlockFace.UP, i).type = Material.GLASS

        }
    }

    private fun findBlocksInCube(location1: Location, location2: Location, blockToFind: Material): MutableList<Location> {
        val world: World = location1.world
        val foundedBlocks: MutableList<Location> = mutableListOf()

        val minX = Math.min(location1.blockX, location2.blockX)
        val minY = Math.min(location1.blockY, location2.blockY)
        val minZ = Math.min(location1.blockZ, location2.blockZ)

        val maxX = Math.max(location1.blockX, location2.blockX)
        val maxY = Math.max(location1.blockY, location2.blockY)
        val maxZ = Math.max(location1.blockZ, location2.blockZ)

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val blockLocation = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                    val block = blockLocation.block

                    if (block.type == blockToFind) {
                        foundedBlocks.add(blockLocation)
                    }
                }
            }
        }

        return foundedBlocks
    }

    private fun populateBlocksBetweenCorners() {
        if (corner1 != null && corner2 != null) {
            val world: World = corner1.world
            val minX = Math.min(corner1.blockX, corner2.blockX)
            val minY = Math.min(corner1.blockY, corner2.blockY)
            val minZ = Math.min(corner1.blockZ, corner2.blockZ)
            val maxX = Math.max(corner1.blockX, corner2.blockX)
            val maxY = Math.max(corner1.blockY, corner2.blockY)
            val maxZ = Math.max(corner1.blockZ, corner2.blockZ)

            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        val blockLocation = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                        val block = blockLocation.block
//                        arenaBlocks.add(block)
                    }
                }
            }
        }
    }

    private fun validateCorners() {
        if (corner1?.world != corner2?.world || corner1?.world?.name != corner2?.world?.name) {
            throw IllegalArgumentException("Corners must be in the same world")
        }
    }

}
