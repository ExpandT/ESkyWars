package org.expandt.eskywars.guis

import org.bukkit.ChatColor
import org.expandt.eskywars.kit.KitManager

class KitSelectorGui: BasicGui(27, "${ChatColor.RED}Выбор Набора")  {
    override fun initialize() {
        val kitManager = KitManager()
        var slotIndex = 10


        kitManager.getKitsListForGui()?.forEach {
            addItem(it, slotIndex)
            slotIndex++
        }
    }
}
