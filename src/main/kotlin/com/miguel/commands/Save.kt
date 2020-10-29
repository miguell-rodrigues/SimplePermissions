package com.miguel.commands

import com.miguel.manager.PermissionManager
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Save : BukkitCommand("save") {

    override fun execute(sender: CommandSender, label: String, strings: Array<out String>): Boolean {

        if (sender is Player) {
            if (!sender.hasPermission("*") && !sender.isOp)
                return true
        }

        PermissionManager.saveAll()

        sender.sendMessage("§fPermissões salvas com sucesso§a!")

        return false
    }
}