package org.expandt.eskywars.guis

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

interface GuiItem {
    fun onClick(event: InventoryClickEvent)

    fun toItemStack(): ItemStack

    class Builder {
        private var material: Material = Material.AIR
        private var displayName = ""
        private var onClick: ((InventoryClickEvent) -> Unit)? = null

        fun stack(material: Material): Builder {
            this.material = material
            return this
        }

        fun displayName(name: String): Builder {
            this.displayName = name
            return this
        }

        fun onClick(onClick: (InventoryClickEvent) -> Unit): Builder {
            this.onClick = onClick
            return this
        }

        fun build(): GuiItem {
            require(onClick != null) { "onClick must be set" }

            return object : GuiItem {
                override fun onClick(event: InventoryClickEvent) {
                    onClick?.invoke(event)
                }

                override fun toItemStack(): ItemStack {
                    val itemStack = ItemStack(material)
                    val meta: ItemMeta = itemStack.itemMeta

                    meta.setDisplayName(displayName)

                    itemStack.itemMeta = meta

                    return itemStack
                }
            }
        }

    }
}
