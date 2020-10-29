package com.miguel.manager

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object PlayerManager {

    fun addPermission(uuid: UUID, permission: String): Boolean {
        val permissiblePlayer = PermissionManager.getPermissiblePlayer(uuid)

        val result = permissiblePlayer.addPermission(permission)

        if (Bukkit.getPlayer(uuid) != null) {
            val player = Bukkit.getPlayer(uuid)

            val attachment = PermissionManager.getAttachment(player)

            attachment.setPermission(permission, true)

            if (permission == "*") {
                player.isOp = true
            }

            player.recalculatePermissions()
        }

        return result
    }

    fun removePermission(uuid: UUID, permission: String): Boolean {
        val permissiblePlayer = PermissionManager.getPermissiblePlayer(uuid)

        val result = permissiblePlayer.removePermission(permission)

        if (Bukkit.getPlayer(uuid) != null) {
            val player = Bukkit.getPlayer(uuid)

            val attachment = PermissionManager.getAttachment(player)

            val permissibleGroup = PermissionManager.getPermissibleGroup(permissiblePlayer.group)

            val permissions = permissibleGroup.getPermissions()

            if (permission !in permissions) {
                attachment.unsetPermission(permission)

                if (permission == "*") {
                    player.isOp = false
                }
            }

            player.recalculatePermissions()
        }

        return result
    }

    fun addPermissions(uuid: UUID, permissions: Array<String>): Boolean {
        var result = false

        permissions.forEach { permission ->
            result = addPermission(uuid, permission)

            if (!result)
                return result
        }

        return result
    }

    fun removePermissions(uuid: UUID, permissions: Array<String>): Boolean {
        var result = false

        val permissiblePlayer = PermissionManager.getPermissiblePlayer(uuid)

        if (permissiblePlayer.getPermissions().containsAll(permissions.toList())) {
            permissions.forEach { permission ->
                result = removePermission(uuid, permission)
            }
        }

        return result
    }

    fun hasPermission(uuid: UUID, permission: String): Boolean {
        val permissiblePlayer = PermissionManager.getPermissiblePlayer(uuid)

        Bukkit.getOperators().forEach {
            return it.uniqueId == uuid
        }

        return permissiblePlayer.getPermissions().contains(permission)
    }

    fun reloadPlayerPermissions(player: Player?) {
        if (player != null) {
            PermissionManager.removePermissions(player)

            PermissionManager.injectPermissions(player)

            player.recalculatePermissions()
        }
    }
}