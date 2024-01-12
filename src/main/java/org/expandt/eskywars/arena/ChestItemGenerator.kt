package org.expandt.eskywars.arena

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.expandt.eskywars.ESkyWars
import java.io.File

data class ChestConfig(
    val weapons: List<Pair<String, Int>>,
    val blocks: List<Pair<String, Int>>,
    val food: List<Pair<String, Int>>,
    val other: List<Pair<String, Int>>
)

class ChestItemGenerator {

    private val configFile = File(ESkyWars.INSTANCE.dataFolder, "config.yml")
    private val config = YamlConfiguration.loadConfiguration(configFile)

    fun generateItems(): List<ItemStack> {
        val config = loadChestConfig()

        val weapon = getItemStack(config?.weapons?.random()!!)
        val block = getItemStack(config?.blocks?.random()!!)
        val food = getItemStack(config?.food?.random()!!)
        val other = getItemStack(config?.other?.random()!!)

        return weapon + block + food + other
    }
    private fun readItemCategory(category: String): List<Pair<String, Int>> {
        val itemList = mutableListOf<Pair<String, Int>>()
        val categorySection = config.getConfigurationSection("chest_items.$category") ?: return itemList

        for (key in categorySection.getKeys(false)) {
            val amount = categorySection.getInt(key, 1)
            itemList.add(Pair(key, amount))
        }

        return itemList
    }

    private fun loadChestConfig(): ChestConfig {
        val weapons = readItemCategory("weapons")
        val blocks = readItemCategory("blocks")
        val food = readItemCategory("food")
        val other = readItemCategory("other")

        return ChestConfig(weapons, blocks, food, other)
    }

    private fun getItemStack(item: Pair<String, Int>): List<ItemStack> {
        return listOf(ItemStack(Material.matchMaterial(item.first)!!, item.second))
    }


}
