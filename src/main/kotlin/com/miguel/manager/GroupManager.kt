package com.miguel.manager

import com.miguel.model.PermissibleGroup
import org.bukkit.Bukkit
import java.util.*

object GroupManager {

    fun createGroup(name: String): Boolean {
        val group = PermissibleGroup(
            name,
            emptyArray()
        )

        return PermissionManager.addGroup(group)
    }

    fun changePlayerGroup(group: String, uuid: UUID): Boolean {
        val permissiblePlayer = PermissionManager.getPermissiblePlayer(uuid)

        if (permissiblePlayer.group != group) {
            permissiblePlayer.group = group

            PlayerManager.reloadPlayerPermissions(Bukkit.getPlayer(uuid))

            return true
        }

        return false
    }

    fun addPermission(group: String, permission: String): Boolean {
        val permissibleGroup = PermissionManager.getPermissibleGroup(group)

        val success = permissibleGroup.addPermission(permission)

        val groupPlayers = PermissionManager.getGroupPlayers(group)

        groupPlayers.forEach { t ->
            if (Bukkit.getPlayer(t.uuid) != null) {
                val player = Bukkit.getPlayer(t.uuid)

                if (player != null) {
                    PermissionManager.getAttachment(player)?.setPermission(permission, true)

                    player.recalculatePermissions()
                }


            }
        }

        return success
    }

    fun addPermissions(group: String, permissions: Array<String>): Boolean {
        var result = false

        permissions.forEach { permission ->
            result = addPermission(group, permission)

            if (!result)
                return result
        }

        return result
    }

    fun removePermission(group: String, permission: String): Boolean {
        val permissibleGroup = PermissionManager.getPermissibleGroup(group)

        val success = permissibleGroup.removePermission(permission)

        val groupPlayers = PermissionManager.getGroupPlayers(group)

        groupPlayers.forEach { t ->
            if (Bukkit.getPlayer(t.uuid) != null) {
                val player = Bukkit.getPlayer(t.uuid)

                if (player != null) {
                    PermissionManager.getAttachment(player).unsetPermission(permission)

                    player.recalculatePermissions()
                }
            }
        }

        return success
    }

    fun removePermissions(group: String, permissions: Array<String>): Boolean {
        var result = false

        val permissibleGroup = PermissionManager.getPermissibleGroup(group)

        if (permissibleGroup.getPermissions().containsAll(permissions.toList())) {
            permissions.forEach { permission ->
                result = removePermission(group, permission)
            }
        }

        return result
    }
}