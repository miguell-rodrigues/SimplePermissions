package com.miguel

import com.miguel.commands.Group
import com.miguel.commands.User
import com.miguel.commands.Reload
import com.miguel.commands.Save
import com.miguel.config.JsonConfig
import com.miguel.listener.PermissibleEvents
import com.miguel.manager.GroupManager
import com.miguel.manager.PermissionManager
import com.miguel.manager.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.plugin.java.JavaPlugin

class SimplePermissions : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: JavaPlugin
    }

    override fun onLoad() {
        INSTANCE = this

        dataFolder.mkdir()

        JsonConfig.init()

        PermissionManager.loadAll()

        GroupManager.createGroup("default")
    }

    override fun onEnable() { 
        (server as CraftServer).commandMap.register("user", User())
        (server as CraftServer).commandMap.register("group", Group())
        (server as CraftServer).commandMap.register("reload", Reload())
        (server as CraftServer).commandMap.register("save", Save())

        server.pluginManager.registerEvents(PermissibleEvents(), this)

        Bukkit.getOnlinePlayers().forEach { player ->
            PlayerManager.reloadPlayerPermissions(player)
        }
    }

    override fun onDisable() {
        PermissionManager.saveAll()
    }
}