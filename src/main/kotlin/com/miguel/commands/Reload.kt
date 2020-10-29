package com.miguel.commands

import com.miguel.manager.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Reload : BukkitCommand("reload_permissions") {

    override fun execute(sender: CommandSender, label: String, strings: Array<out String>): Boolean {

        if (sender is Player) {
            if (!sender.hasPermission("*") && !sender.isOp)
                return true
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            PlayerManager.reloadPlayerPermissions(player)
        }

        sender.sendMessage("§fPermissões recarregadas com sucesso§a!")

        return false
    }
}