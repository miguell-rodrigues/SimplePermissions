package com.miguel.model

import java.util.*
import kotlin.collections.ArrayList

class PermissiblePlayer(val uuid: UUID, var group: String, perms: Array<String>) {

    private val permissions: MutableList<String> = ArrayList()

    init {
        permissions.addAll(perms)
    }

    fun addPermission(permission: String): Boolean {
        if (permission !in permissions) {
            permissions.add(permission)
            return true
        }

        return false
    }

    fun removePermission(permission: String): Boolean {
        if (permission in permissions) {
            permissions.remove(permission)
            return true
        }

        return false
    }

    fun getPermissions(): List<String> {
        return permissions
    }

    fun changeGroup(group: String) {
        this.group = group
    }

    fun clone(): PermissiblePlayer {
        return PermissiblePlayer(this.uuid, this.group, this.permissions.toTypedArray())
    }

    override fun toString(): String {
        return "PermissiblePlayer(uuid=$uuid, group='$group', permissions=$permissions)"
    }
}