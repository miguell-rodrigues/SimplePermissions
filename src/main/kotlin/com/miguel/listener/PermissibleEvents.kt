package com.miguel.listener

import com.miguel.manager.PermissionManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class PermissibleEvents : Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        val uniqueId = event.uniqueId

        PermissionManager.load(uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player

        PermissionManager.injectPermissions(player)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        PermissionManager.uninject(player)
    }
}