package org.expandt.eskywars.arena

import com.google.gson.annotations.Expose
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import org.expandt.eskywars.ESkyWars


class Arena (
    @Expose val name: String,
    @Expose val corner1: Location?,
    @Expose val corner2: Location?,
    @Expose val timeToStart: Int = 5,
    @Expose val minPlayersToStart: Int = 2,
    @Expose var lobby: Location?,
    @Expose private val arenaBlocks: MutableList<Block> = mutableListOf(),
    @Expose private val players: MutableList<Player> = ArrayList(),
    @Expose private var emeraldBlocks: MutableList<Location> = mutableListOf(),
    @Expose private var chestList: MutableList<Location> = mutableListOf(),
) {

    init {
        validateCorners()
        populateBlocksBetweenCorners()
        chestList = findBlocksInCube(corner1!!, corner2!!, Material.CHEST)
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
            player.teleport(teleportLocation)
        }
    }

    fun startPreparation() {
        createGlassCageAroundEmerald(corner1!!, corner2!!)
        changePlayersGamemode(GameMode.ADVENTURE)
        generateChestLoot()
        setGameScoreboard()
        teleportPlayers()

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

        if (players.size == 1) {
            endGame()
        }
    }

    fun restartArena() {
        arenaBlocks.forEach {
            val blockLocation = it.location
            val world = blockLocation.world
            val x = blockLocation.blockX
            val y = blockLocation.blockY
            val z = blockLocation.blockZ
            world.getBlockAt(x, y, z).type = it.type
        }
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

    private fun endGame() {
        val winner = players.first()

        winner.gameMode = GameMode.ADVENTURE
        sendArenaMessage("${ChatColor.GRAY}Игрок ${ChatColor.RED}${winner.name} победил!")
        for (player in players) {
            player.scoreboard.clearSlot(DisplaySlot.SIDEBAR)
            player.teleport(lobby!!)
        }
    }

    private fun setGameScoreboard() {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective = scoreboard.registerNewObjective("Sw", "dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR
        objective.displayName = "${ChatColor.RED}${ChatColor.BOLD}ESkyWars"

        val arenaScore = objective.getScore("${ChatColor.GRAY}АРЕНА - ${ChatColor.RED}${name}")
        arenaScore.score = 4

        val emptyLine1Score = objective.getScore(" ")
        emptyLine1Score.score = 3

        val playersCountScore = objective.getScore("${ChatColor.GRAY}Игроков: ${ChatColor.RED}${players.size}/${emeraldBlocks.size}")
        playersCountScore.score = 2

        val emptyLine2Score = objective.getScore(" ")
        emptyLine2Score.score = 1

        for (player in players) {
            player.scoreboard = scoreboard
        }
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

    private fun createGlassCageAroundEmerald(location1: Location, location2: Location) {
        emeraldBlocks = findBlocksInCube(location1, location2, Material.EMERALD_BLOCK)

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
                        arenaBlocks.add(block)
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
