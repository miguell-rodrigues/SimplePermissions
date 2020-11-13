package com.miguel.commands

import com.miguel.manager.GroupManager
import com.miguel.manager.PermissionManager
import com.miguel.util.UUIDFetcher
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.util.*

class Group : BukkitCommand("group") {

    override fun execute(sender: CommandSender, label: String, strings: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("*") && !sender.isOp)
                return true
        }

        if (strings.isEmpty()) {
            sender.sendMessage("§c/group [name] [add, remove] [player | permission]")
            sender.sendMessage("§c/group [name] [create | members | permissions | delete]")
            sender.sendMessage("§c/group [list]")
        } else {
            val s = strings[0]

            when (strings.size) {
                1 -> {
                    if (s == "list") {
                        val allGroups = PermissionManager.getAllGroups()

                        sender.sendMessage(" ")
                        sender.sendMessage("§fGrupos registrados §a- §e${allGroups.size}")
                        sender.sendMessage(" ")

                        allGroups.forEach { group ->
                            sender.sendMessage(" §f- §a${group.name}")
                        }

                        sender.sendMessage(" ")
                    }
                }

                2 -> {
                    when (strings[1]) {
                        "create" -> {
                            if (GroupManager.createGroup(s)) {
                                sender.sendMessage("§fGrupo §e$s §fCriado com sucesso §a!")
                            } else {
                                sender.sendMessage("§cO grupo §e$s §cjá existe §e!")
                            }
                        }

                        "members" -> {
                            val group = PermissionManager.getPermissibleGroup(s)

                            if (group.name == "undefined") {
                                sender.sendMessage("§cO grupo §e$s §cnão existe!")
                                return true
                            }

                            val groupPlayers = PermissionManager.getGroupPlayers(s)

                            if (groupPlayers.isNotEmpty()) {
                                sender.sendMessage("§fMembros do grupo §a- §e$s")
                                sender.sendMessage(" ")

                                groupPlayers.forEach { permissiblePlayer ->
                                    val name: String? = if (Bukkit.getPlayer(permissiblePlayer.uuid) == null) {
                                        permissiblePlayer.uuid.toString().replace("-", "")
                                    } else {
                                        Bukkit.getPlayer(permissiblePlayer.uuid)?.name
                                    }

                                    sender.sendMessage(" §f- §a$name")
                                }

                                sender.sendMessage(" ")
                            } else {
                                sender.sendMessage("§fEsse grupo ainda não possui nenhum membro")
                            }
                        }

                        "permissions" -> {
                            val group = PermissionManager.getPermissibleGroup(s)

                            if (group.name == "undefined") {
                                sender.sendMessage("§cEsse grupo não existe§e!")
                            } else {
                                val permissions = group.getPermissions()

                                if (permissions.isNotEmpty()) {
                                    sender.sendMessage(" ")
                                    sender.sendMessage("§fPermissões do grupo §a- §e$s")
                                    sender.sendMessage(" ")

                                    permissions.forEach { permission ->
                                        sender.sendMessage(" §f- §a$permission")
                                    }

                                    sender.sendMessage(" ")
                                } else {
                                    sender.sendMessage("§fEsse grupo ainda não possui nenhuma permissão")
                                }
                            }
                        }

                        "delete" -> {
                            if (PermissionManager.deleteGroup(s)) {
                                sender.sendMessage("§fGrupo §e$s §fDeletado com sucesso §a!")
                            } else {
                                sender.sendMessage("§cEsse grupo não existe§e!")
                            }
                        }
                    }
                }

                3 -> {
                    var pp = strings[2]

                    val group = PermissionManager.getPermissibleGroup(s)

                    if (group.name == "undefined") {
                        sender.sendMessage("§cO grupo §e$s §cnão existe!")
                        return true
                    }

                    var targetPlayer: UUID? = null

                    if (Bukkit.getPlayer(pp) != null) {
                        targetPlayer = Bukkit.getPlayer(pp)?.uniqueId
                    } else {
                        try {
                            targetPlayer = UUIDFetcher.getUUID(pp)
                        } catch (e: Exception) {
                        }
                    }

                    when (strings[1]) {
                        "add" -> {
                            if (targetPlayer == null) {
                                if (pp.isEmpty()) {
                                    sender.sendMessage("§cPermissão inválida§e!")
                                    return true
                                }

                                if (pp.startsWith("[") && pp.endsWith("]")) {
                                    pp = pp.replace("[", "").replace("]", "")

                                    val permissions = ArrayList<String>()

                                    permissions.addAll(pp.split(","))

                                    if (permissions.isNotEmpty()) {
                                        if (GroupManager.addPermissions(s, permissions.toTypedArray())) {
                                            sender.sendMessage("§fPermissões §e$permissions §fAdicionadas ao grupo §a$s")
                                        } else {
                                            sender.sendMessage("§fO grupo já possui uma das permissões listadas§e!")
                                        }
                                    }
                                } else {
                                    if (GroupManager.addPermission(s, pp)) {
                                        sender.sendMessage("§fPermissão §e$pp §fAdicionada ao grupo §a$s")
                                    } else {
                                        sender.sendMessage("§fO grupo §e$s §fJá possui a permissão §a$pp §e!")
                                    }
                                }

                            } else {
                                if (GroupManager.changePlayerGroup(s, targetPlayer)) {
                                    sender.sendMessage("§fJogador §e$pp §fAdicionado ao grupo §a$s")
                                } else {
                                    sender.sendMessage("§fO jogador §e$pp §fJá está no grupo §a$s §e!")
                                }
                            }
                        }

                        "remove" -> {
                            if (targetPlayer == null) {
                                if (pp.isEmpty()) {
                                    sender.sendMessage("§cPermissão inválida§e!")
                                    return true
                                }

                                if (pp.startsWith("[") && pp.endsWith("]")) {
                                    pp = pp.replace("[", "").replace("]", "")

                                    val permissions = ArrayList<String>()

                                    permissions.addAll(pp.split(","))

                                    if (permissions.isNotEmpty()) {
                                        if (GroupManager.removePermissions(s, permissions.toTypedArray())) {
                                            sender.sendMessage("§fPermissões §e$permissions §fRemovidas ao grupo §a$s")
                                        } else {
                                            sender.sendMessage("§fO grupo não possui uma das permissões listadas§e!")
                                        }
                                    }
                                } else {
                                    if (GroupManager.removePermission(s, pp)) {
                                        sender.sendMessage("§fPermissão §e$pp §fRemovida do grupo §a$s")
                                    } else {
                                        sender.sendMessage("§fO grupo §e$s §fNão possui a permissão §a$pp §e!")
                                    }
                                }

                            } else {
                                if (GroupManager.changePlayerGroup("default", targetPlayer)) {
                                    sender.sendMessage("§fJogador §e$pp §fRemovido do grupo §a$s")
                                } else {
                                    sender.sendMessage("§fO jogador §e$pp §fJá foi removido ou está no grupo default§e!")
                                }
                            }
                        }
                    }
                }

                else -> {
                    sender.sendMessage("§c/group [name] [add, remove] [player | permission]")
                    sender.sendMessage("§c/group [name] [create | members | permissions | delete]")
                    sender.sendMessage("§c/group [list]")
                }
            }
        }

        return false
    }
}