package fr.army.stelyteam.listener;

import fr.army.stelyteam.StelyTeamPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class ListenerLoader {

    public void registerListeners(StelyTeamPlugin plugin) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryClickListener(plugin), plugin);
        pluginManager.registerEvents(new InventoryCloseListener(plugin), plugin);
        pluginManager.registerEvents(new ConvCacheListener(plugin.getCacheManager()), plugin);
        pluginManager.registerEvents(new TeamCacheListener(plugin.getTeamCache()), plugin);
        pluginManager.registerEvents(new ChatPrefixListener(plugin.getTeamCache()), plugin);
    }

}
