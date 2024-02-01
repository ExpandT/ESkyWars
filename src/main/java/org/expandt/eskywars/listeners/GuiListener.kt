package org.expandt.eskywars.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.expandt.eskywars.guis.BasicGui

class GuiListener: Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        (event.inventory.holder as? BasicGui)?.onClick(event)
    }
}
