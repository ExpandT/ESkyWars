package org.expandt.eskywars.utils

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.expandt.eskywars.ESkyWars

class ChatUtil {

    private val defaultSignature = "${ChatColor.GRAY}[ ${ESkyWars.INSTANCE.name} ] >>> "
    private val errorSignature = "&${ChatColor.RED}[ ${ESkyWars.INSTANCE.name} &cERROR] >>> "

    fun sendMessage(player: Player, msg: String, isError: Boolean) {
        if (isError) {
            player.sendMessage(errorSignature + msg)
        } else {
            player.sendMessage(defaultSignature + msg)
        }
    }
}
