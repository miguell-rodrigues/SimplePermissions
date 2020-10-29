package com.miguel.model

class PermissibleGroup(val name: String, perms: Array<String>) {

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

    fun clone(): PermissibleGroup {
        return PermissibleGroup(this.name, this.permissions.toTypedArray())
    }
}