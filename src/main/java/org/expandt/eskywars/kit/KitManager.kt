package org.expandt.eskywars.kit

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.persistence.PersistentDataType
import org.expandt.eskywars.ESkyWars
import org.expandt.eskywars.guis.GuiItem
import java.io.File


class KitManager {

    private val kitsConfigFile = File(ESkyWars.INSTANCE.dataFolder, "config.yml")
    private val kitsConfig = YamlConfiguration.loadConfiguration(kitsConfigFile)



    fun getKitsListForGui(): List<GuiItem>? {
        val kitsSection = kitsConfig.getConfigurationSection("kits")

        return kitsSection?.getKeys(false)?.map { kitName ->

            val kitInfo = kitsSection.getConfigurationSection(kitName)
            val kitDisplayName = kitInfo?.getString("name", "Набор без названия)")
            val kitIcon = kitInfo?.getString("icon", "STONE")
            val kitIconMaterial = kitIcon?.let { it1 -> Material.matchMaterial(it1) } ?: Material.STONE

            val kitItem = ItemStack(kitIconMaterial)
            val kitMeta = kitItem.itemMeta
            kitMeta?.setDisplayName(kitDisplayName)
            kitItem.itemMeta = kitMeta

            GuiItem.Builder()
                .stack(kitIconMaterial)
                .displayName(kitDisplayName!!)
                .onClick {
                    val player = it.whoClicked as Player
                    player.sendMessage("${ChatColor.GREEN}Выбран набор: ${kitDisplayName}")
                    player.setMetadata("kit", FixedMetadataValue(ESkyWars.INSTANCE, kitName))
                    player.closeInventory()
                }
                .build()
        }
    }

    fun applyKit(player: Player, kitName: String) {
        val kitSection = kitsConfig.getConfigurationSection("kits.$kitName.items") ?: return

        for (itemName in kitSection.getKeys(false)) {
            val itemMaterial = Material.matchMaterial(itemName) ?: continue
            val quantity = kitSection.getInt("$itemName.quantity", 1)
            val item = ItemStack(itemMaterial, quantity)

            if (kitSection.contains("$itemName.enchantment")) {
                val enchantmentName = kitSection.getString("$itemName.enchantment") ?: continue
                val enchantment = Enchantment.getByName(enchantmentName) ?: continue
                val level = kitSection.getInt("$itemName.enchantment_level", 1)
                item.addUnsafeEnchantment(enchantment, level)
            }

            player.inventory.addItem(item)
        }
    }

    private fun clearInventoryExcept(player: Player) {
        val inventory = player.inventory
        for (slot in 0 until inventory.size) {
            val currentItem = inventory.getItem(slot)
            if (!currentItem?.itemMeta?.persistentDataContainer?.has(
                    NamespacedKey(ESkyWars.INSTANCE, "kit_selector"), PersistentDataType.STRING)!!) {
                inventory.setItem(slot, null)
            }
        }
    }

}
