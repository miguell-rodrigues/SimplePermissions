package com.miguel.manager

import com.miguel.SimplePermissions
import com.miguel.config.JsonConfig
import com.miguel.model.PermissibleGroup
import com.miguel.model.PermissiblePlayer
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object PermissionManager {

    private val INSTANCE = SimplePermissions.INSTANCE

    /*Players*/
    private val permissionMap: HashMap<UUID, PermissionAttachment> = HashMap()

    private val permissiblePlayers: HashMap<UUID, PermissiblePlayer> = HashMap()
    /*End players*/

    /*Groups*/
    private val permissibleGroups: HashMap<String, PermissibleGroup> = HashMap()
    /*End groups*/

    /**
     * Armazena um objeto PermissiblePlayer para um UUID que não está contido na map.
     * @param [uuid] UUID a ser armazenado
     */
    fun load(uuid: UUID) {
        if (uuid !in permissiblePlayers) {
            permissiblePlayers[uuid] = PermissiblePlayer(uuid, "default", emptyArray())
        }
    }

    /**
     * Injeta permissões em um jogador
     * @param [player] Jogador a injetar as permissões
     */
    fun injectPermissions(player: Player) {
        val permissionAttachment = getAttachment(player)

        val permissiblePlayer = if (permissiblePlayers.containsKey(player.uniqueId)) {
            permissiblePlayers[player.uniqueId]!!
        } else {
            load(player.uniqueId)
            permissiblePlayers[player.uniqueId]!!
        }

        val permissions = permissiblePlayer.getPermissions().toMutableList()

        val group = getPermissibleGroup(permissiblePlayer.group)

        permissions.addAll(group.getPermissions())

        if (permissions.contains("*")) {
            player.isOp = true
        }

        permissions.forEach { t ->
            permissionAttachment.setPermission(t, true)
        }
    }

    /**
     * @param [player] Jogador a pegar o attachment
     * @return PermissionAttachment
     */
    fun getAttachment(player: Player): PermissionAttachment {
        return if (player.uniqueId in permissionMap) {
            permissionMap[player.uniqueId]!!
        } else {
            val attachment = player.addAttachment(INSTANCE)
            permissionMap[player.uniqueId] = attachment

            attachment
        }
    }

    /**
     * Remove o PermissionAttachment anteriormente setado em
     * @see injectPermissions
     * @param [player] Jogador a remover o permissionAttachment
     */
    fun uninject(player: Player) {
        if (player.uniqueId in permissionMap) {
            permissionMap[player.uniqueId]?.let { player.removeAttachment(it) }
            permissionMap.remove(player.uniqueId)
        }
    }

    /**
     * Remove as permissões de um jogador durante o jogo
     * @param [player] Jogador a remover as permissões
     */
    fun removePermissions(player: Player) {
        val attachment = getAttachment(player)

        val permissions = attachment.permissions

        permissions.keys.forEach { permission -> attachment.unsetPermission(permission) }

        player.recalculatePermissions()
    }

    /**
     * Carrega os dados armazenados
     */
    fun loadAll() {
        val allGroups = JsonConfig.getAllGroups()

        allGroups.forEach { t ->
            permissibleGroups[t.name] = t
        }

        val allPlayers = JsonConfig.getAllPlayers()

        allPlayers.forEach { t ->
            permissiblePlayers[t.uuid] = t
        }
    }

    /**
     * Salva as permissões e grupos
     */
    fun saveAll() {
        permissiblePlayers.values.forEach { permissiblePlayer ->
            JsonConfig.createPlayer(permissiblePlayer)
        }

        permissibleGroups.values.forEach { t ->
            val groupPlayers = getGroupPlayers(t.name)

            if (groupPlayers.isEmpty()) {
                JsonConfig.deleteGroup(t)
            } else {
                JsonConfig.createGroup(t)
            }
        }
    }

    /**
     * @param [uuid] UUID do jogador
     * @return Objeto PermissiblePlayer
     * @see PermissiblePlayer
     */
    fun getPermissiblePlayer(uuid: UUID): PermissiblePlayer {
        return when (uuid) {
            in permissiblePlayers -> {
                permissiblePlayers[uuid]!!
            }
            else -> {
                val permissiblePlayer = PermissiblePlayer(uuid, "default", emptyArray())

                permissiblePlayers[uuid] = permissiblePlayer

                permissiblePlayers[uuid]!!
            }
        }
    }

    /**
     * @param [name] Nome do grupo
     * @return Objeto PermissibleGroup
     * @see PermissibleGroup
     */
    fun getPermissibleGroup(name: String): PermissibleGroup {
        return if (permissibleGroups.containsKey(name)) {
            permissibleGroups[name]!!
        } else {
            PermissibleGroup("undefined", emptyArray())
        }
    }

    /**
     * @return Lita com todos os grupos
     * @see PermissibleGroup
     */
    fun getAllGroups(): List<PermissibleGroup> {
        return permissibleGroups.values.toList()
    }

    /**
     * @return Lita com todos os jogadores
     * @see PermissiblePlayer
     */
    fun getAllPlayers(): List<PermissiblePlayer> {
        return permissiblePlayers.values.toList()
    }

    /**
     * @param [name] Nome do grupo
     * @return Lista com todos os membros do grupo
     * @see PermissiblePlayer
     */
    fun getGroupPlayers(name: String): List<PermissiblePlayer> {
        val list: MutableList<PermissiblePlayer> = ArrayList()

        permissiblePlayers.values.forEach { t ->
            if (t.group == name) {
                list.add(t)
            }
        }

        return list
    }

    /**
     * Adiciona um grupo
     * @param [group] Grupo a ser adicionado
     * @return True se o grupo for adicionado e false caso não
     */
    fun addGroup(group: PermissibleGroup): Boolean {
        if (group.name !in permissibleGroups) {
            permissibleGroups[group.name] = group

            println("Created group: $group.name")

            return true
        }

        return false
    }

    /**
     * Deleta um determinado grupo
     * @param [group] Nome do grupo a ser deletado
     */
    fun deleteGroup(group: String): Boolean {
        val groupPlayers = getGroupPlayers(group)

        if (groupPlayers.isNotEmpty()) {
            groupPlayers.forEach { permissiblePlayer ->
                permissiblePlayer.group = "default"
            }

            return true
        }

        return false
    }
}