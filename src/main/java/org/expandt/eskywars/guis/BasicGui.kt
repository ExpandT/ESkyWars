package org.expandt.eskywars.guis

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

abstract class BasicGui( guiSize: Int = 9, guiName: String = "Default") : InventoryHolder {
    val items = mutableMapOf<Int, GuiItem>()
    private val inventory: Inventory = Bukkit.createInventory(this, guiSize, guiName)

    init {
        inventory.contents.fill(ItemStack(Material.WHITE_STAINED_GLASS_PANE))
    }

    abstract fun initialize()

    fun onClick(event: InventoryClickEvent) {
        if (event.inventory == inventory) {
            event.isCancelled = true
            val clickedSlot = event.slot
            val guiItem = items[clickedSlot]
            guiItem?.onClick(event)
        }
    }

    fun addItem(item: GuiItem, slot: Int) {
        items[slot] = item
        inventory.setItem(slot, item.toItemStack())
    }

    fun open(player: Player) {
        player.openInventory(inventory)
    }

    fun close(player: Player) {
        player.closeInventory()
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}
