package com.miguel.commands

import com.miguel.manager.PermissionManager
import com.miguel.manager.PlayerManager
import com.miguel.util.UUIDFetcher
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

class User : BukkitCommand("user") {

    override fun execute(sender: CommandSender, label: String, strings: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("*") && !sender.isOp)
                return true
        }

        if (strings.isNotEmpty()) {
            if (strings.size == 3 || strings.size == 2) {
                val s = strings[0]

                var targetPlayer: UUID?

                targetPlayer = if (Bukkit.getPlayer(s) != null) {
                    Bukkit.getPlayer(s)?.uniqueId
                } else {
                    try {
                        UUIDFetcher.getUUID(s)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (targetPlayer == null) {
                    targetPlayer = UUID.nameUUIDFromBytes("OfflinePlayer:${s}".toByteArray(StandardCharsets.UTF_8))
                }

                var permission = if (strings.size == 3) strings[2] else ""

                when (strings[1].toLowerCase()) {
                    "add" -> {
                        if (permission.isEmpty()) {
                            sender.sendMessage("§cPermissão inválida§e!")
                            return true
                        }

                        if (targetPlayer == null) {
                            sender.sendMessage("§cNão foi possível localizar esse jogador§e!")
                        } else {
                            if (permission.startsWith("[") && permission.endsWith("]")) {
                                permission = permission.replace("[", "").replace("]", "")

                                val permissions = ArrayList<String>()

                                permissions.addAll(permission.split(","))

                                if (permissions.isNotEmpty()) {
                                    if (PlayerManager.addPermissions(targetPlayer, permissions.toTypedArray())) {
                                        sender.sendMessage("§fPermissões §e$permissions §fAdicionadas ao jogador §a$s")
                                    } else {
                                        sender.sendMessage("§fO jogador já possui uma das permissões listadas§e!")
                                    }
                                }
                            } else {
                                if (PlayerManager.addPermission(targetPlayer, permission)) {
                                    sender.sendMessage("§fPermissão §e$permission §fAdicionada ao jogador §a$s")
                                } else {
                                    sender.sendMessage("§fO jogador §e$s §fjá possui a permissão §a$permission")
                                }
                            }
                        }
                    }

                    "remove" -> {
                        if (permission.isEmpty()) {
                            sender.sendMessage("§cPermissão inválida§e!")
                            return true
                        }

                        if (targetPlayer == null) {
                            sender.sendMessage("§cNão foi possível localizar esse jogador§e!")
                        } else {
                            if (permission.startsWith("[") && permission.endsWith("]")) {
                                permission = permission.replace("[", "").replace("]", "")

                                val permissions = ArrayList<String>()

                                permissions.addAll(permission.split(","))

                                if (permissions.isNotEmpty()) {
                                    if (PlayerManager.removePermissions(targetPlayer, permissions.toTypedArray())) {
                                        sender.sendMessage("§fPermissões §e$permissions §fRemovidas do jogador §a$s")
                                    } else {
                                        sender.sendMessage("§fO jogador não possui uma das permissões listadas§e!")
                                    }
                                }
                            } else {
                                if (PlayerManager.removePermission(targetPlayer, permission)) {
                                    sender.sendMessage("§fPermissão §e$permission §fRemovida do jogador §a$s")
                                } else {
                                    sender.sendMessage("§fO jogador §e$s §fNão possui a permissão §a$permission")
                                }
                            }
                        }
                    }

                    "permissions" -> {
                        if (targetPlayer == null) {
                            sender.sendMessage("§cJogador não encontrado§e!")
                        } else {
                            val permissiblePlayer = PermissionManager.getPermissiblePlayer(targetPlayer)

                            val permissions = permissiblePlayer.getPermissions()

                            if (permissions.isNotEmpty()) {
                                sender.sendMessage(" ")
                                sender.sendMessage("§fPermissões do jogador §a- §e$s")
                                sender.sendMessage(" ")
                                sender.sendMessage("§fGrupo §b§n${permissiblePlayer.group}")
                                sender.sendMessage(" ")


                                permissions.forEach { _permission ->
                                    sender.sendMessage(" §f- §a$_permission")
                                }

                                sender.sendMessage(" ")
                            } else {
                                sender.sendMessage("§fEsse jogador ainda não possui nenhuma permissão")
                            }
                        }
                    }

                    else -> {
                        sender.sendMessage("§c/user [name] [add, remove] [permission]")
                        sender.sendMessage("§c/user [name] [permissions]")
                    }
                }
            } else {
                if (strings.size == 1 && strings[0].toLowerCase() == "list") {

                    val allPlayers = PermissionManager.getAllPlayers()

                    sender.sendMessage(" ")
                    sender.sendMessage("§fJogadores registrados §a- §e${allPlayers.size}")
                    sender.sendMessage(" ")
                    sender.sendMessage(" ")

                    allPlayers.forEach { user ->
                        val uuid = user.uuid.toString().replace("-", "")
                        if (Bukkit.getPlayer(user.uuid) != null) {
                            sender.sendMessage(" §a- §f${Bukkit.getPlayer(user.uuid)?.name} §e- §f$uuid")
                        } else {
                            sender.sendMessage(" §a- §f$uuid")
                        }
                    }

                    sender.sendMessage(" ")
                }
            }
        } else {
            sender.sendMessage("§c/user [name] [add, remove] [permission]")
            sender.sendMessage("§c/user [name] [permissions]")
            sender.sendMessage("§c/user [list]")
        }

        return false
    }
}