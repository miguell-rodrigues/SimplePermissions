package com.miguel.config

import com.google.gson.*
import com.miguel.SimplePermissions
import com.miguel.model.PermissibleGroup
import com.miguel.model.PermissiblePlayer
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object JsonConfig {

    private val players = File(SimplePermissions.INSTANCE.dataFolder, "players.json")
    private val groups = File(SimplePermissions.INSTANCE.dataFolder, "groups.json")

    private val gson = Gson()

    fun init() {
        if (!players.exists()) {
            players.createNewFile()

            val player = JsonArray()

            FileUtils.writeStringToFile(
                players,
                gson.toJson(player),
                    "UTF-8"
            )
        }

        if (!groups.exists()) {
            groups.createNewFile()

            val group = JsonArray()

            FileUtils.writeStringToFile(
                groups,
                gson.toJson(group),
                    "UTF-8"
            )
        }
    }

    private fun addPermission(uuid: UUID, permission: String) {
        if (playerExist(uuid)) {
            val players = JsonParser().parse(FileUtils.readFileToString(players)).asJsonArray

            players.forEach { t ->
                val id = UUID.fromString(t.asJsonObject["uuid"].asString)

                if (uuid == id) {
                    val playerObject = t.asJsonObject

                    val permissions = playerObject["permissions"].asJsonArray

                    val primitive = JsonPrimitive(permission)

                    if (!permissions.contains(primitive)) {
                        permissions.add(primitive)

                        write(this.players, players)
                    }
                }
            }
        }
    }

    private fun changeGroup(uuid: UUID, group: String) {
        if (playerExist(uuid)) {
            val players = JsonParser().parse(FileUtils.readFileToString(players)).asJsonArray

            players.forEach { t ->
                val id = UUID.fromString(t.asJsonObject["uuid"].asString)

                if (uuid == id) {
                    val playerObject = t.asJsonObject

                    playerObject.addProperty("group", group)

                    write(this.players, players)
                }
            }
        }
    }

    private fun removePermission(uuid: UUID, permission: String) {
        if (playerExist(uuid)) {
            val players = JsonParser().parse(FileUtils.readFileToString(players)).asJsonArray

            players.forEach { t ->
                val id = UUID.fromString(t.asJsonObject["uuid"].asString)

                if (uuid == id) {
                    val playerObject = t.asJsonObject

                    val permissions = playerObject["permissions"].asJsonArray

                    val primitive = JsonPrimitive(permission)

                    if (permissions.contains(primitive)) {
                        permissions.remove(primitive)

                        write(this.players, players)
                    }
                }
            }
        }
    }

    private fun addPermission(group: String, permission: String) {
        if (groupExist(group)) {
            val groups = JsonParser().parse(FileUtils.readFileToString(groups)).asJsonArray

            groups.forEach { t ->
                val name = t.asJsonObject["name"].asString

                if (group == name) {
                    val groupObject = t.asJsonObject

                    val permissions = groupObject["permissions"].asJsonArray

                    val primitive = JsonPrimitive(permission)

                    if (!permissions.contains(primitive)) {
                        permissions.add(primitive)

                        write(this.groups, groups)
                    }
                }
            }
        }
    }

    private fun removePermission(group: String, permission: String) {
        if (groupExist(group)) {
            val groups = JsonParser().parse(FileUtils.readFileToString(groups)).asJsonArray

            groups.forEach { t ->
                val name = t.asJsonObject["name"].asString

                if (group == name) {
                    val groupObject = t.asJsonObject

                    val permissions = groupObject["permissions"].asJsonArray

                    val primitive = JsonPrimitive(permission)

                    if (permissions.contains(primitive)) {
                        permissions.remove(primitive)

                        write(this.groups, groups)
                    }
                }
            }
        }
    }

    fun createGroup(permissibleGroup: PermissibleGroup) {
        val groups = JsonParser().parse(FileUtils.readFileToString(groups)).asJsonArray

        val name = permissibleGroup.name

        if (groupExist(name)) {
            permissibleGroup.getPermissions().forEach { permission ->
                val storedPermissions = getPermissions(name)
                val permissions = permissibleGroup.getPermissions()

                storedPermissions.forEach { permission ->
                    if (permission !in permissions) {
                        removePermission(name, permission)
                    }
                }

                permissions.forEach { permission ->
                    addPermission(name, permission)
                }

                addPermission(name, permission)
            }
        } else {
            val permissibleObject = JsonParser().parse(gson.toJson(permissibleGroup)).asJsonObject

            groups.add(permissibleObject)

            write(this.groups, groups)
        }
    }

    fun deleteGroup(permissibleGroup: PermissibleGroup) {
        if (groupExist(permissibleGroup.name)) {
            val groups = JsonParser().parse(FileUtils.readFileToString(groups)).asJsonArray

            groups.forEach { t ->
                val name = t.asJsonObject["name"].asString

                if (permissibleGroup.name == name) {
                    groups.remove(t)

                    write(this.groups, groups)
                }
            }
        }
    }

    fun createPlayer(permissiblePlayer: PermissiblePlayer) {
        val players = JsonParser().parse(FileUtils.readFileToString(players)).asJsonArray

        val uuid = permissiblePlayer.uuid

        if (playerExist(uuid)) {
            val storedPermissions = getPermissions(uuid)
            val permissions = permissiblePlayer.getPermissions()

            storedPermissions.forEach { permission ->
                if (permission !in permissions) {
                    removePermission(uuid, permission)
                }
            }

            permissions.forEach { permission ->
                addPermission(uuid, permission)
            }

            changeGroup(uuid, permissiblePlayer.group)
        } else {
            val permissibleObject = JsonParser().parse(gson.toJson(permissiblePlayer)).asJsonObject

            players.add(permissibleObject)

            write(this.players, players)
        }
    }

    private fun playerExist(uuid: UUID): Boolean {
        val players = JsonParser().parse(FileUtils.readFileToString(players)).asJsonArray

        players.forEach { id ->
            val uniqueId = UUID.fromString(id.asJsonObject["uuid"].asString)

            if (uuid == uniqueId) {
                return true
            }
        }

        return false
    }

    private fun groupExist(name: String): Boolean {
        val groups = JsonParser().parse(FileUtils.readFileToString(groups)).asJsonArray

        groups.forEach { t ->
            val nm = t.asJsonObject["name"].asString

            if (name == nm) {
                return true
            }
        }

        return false
    }

    fun getAllPlayers(): MutableList<PermissiblePlayer> {
        val list: MutableList<PermissiblePlayer> = ArrayList()

        val players = JsonParser().parse(FileUtils.readFileToString(players)).asJsonArray

        players.forEach { t ->
            list.add(gson.fromJson(t, PermissiblePlayer::class.java))
        }

        return list
    }

    private fun getPermissions(uuid: UUID): List<String> {
        val list: MutableList<String> = ArrayList()

        if (playerExist(uuid)) {
            val players = JsonParser().parse(FileUtils.readFileToString(players)).asJsonArray

            players.forEach { t ->
                val id = UUID.fromString(t.asJsonObject["uuid"].asString)

                if (uuid == id) {
                    val playerObject = t.asJsonObject

                    val permissions = playerObject["permissions"].asJsonArray

                    permissions.forEach { perm ->
                        list.add(perm.asString)
                    }
                }
            }
        }

        return list
    }

    private fun getPermissions(group: String): List<String> {
        val list: MutableList<String> = ArrayList()

        if (groupExist(group)) {
            val groups = JsonParser().parse(FileUtils.readFileToString(groups)).asJsonArray

            groups.forEach { t ->
                val name = t.asJsonObject["name"].asString

                if (group == name) {
                    val groupObject = t.asJsonObject

                    val permissions = groupObject["permissions"].asJsonArray

                    permissions.forEach { perm ->
                        list.add(perm.asString)
                    }
                }
            }
        }

        return list
    }

    fun getAllGroups(): MutableList<PermissibleGroup> {
        val list: MutableList<PermissibleGroup> = ArrayList()

        val groups = JsonParser().parse(FileUtils.readFileToString(groups)).asJsonArray

        groups.forEach { t ->
            list.add(gson.fromJson(t, PermissibleGroup::class.java))
        }

        return list
    }

    private fun write(file: File, element: JsonElement) {
        FileUtils.writeStringToFile(
            file,
            gson.toJson(element)
        )
    }
}