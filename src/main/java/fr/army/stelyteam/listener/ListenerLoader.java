package fr.army.stelyteam.listener;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.listener.impl.ChatPrefixListener;
import fr.army.stelyteam.listener.impl.InventoryClickListener;
import fr.army.stelyteam.listener.impl.InventoryCloseListener;
import fr.army.stelyteam.listener.impl.OrderReceiverListener;
import fr.army.stelyteam.listener.impl.PlayerJoinListener;
import fr.army.stelyteam.listener.impl.PlayerQuitListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class ListenerLoader {

    public void registerListeners(StelyTeamPlugin plugin) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryClickListener(plugin), plugin);
        pluginManager.registerEvents(new InventoryCloseListener(plugin), plugin);
        pluginManager.registerEvents(new PlayerJoinListener(plugin), plugin);
        pluginManager.registerEvents(new PlayerQuitListener(plugin), plugin);
        pluginManager.registerEvents(new ChatPrefixListener(), plugin);
        pluginManager.registerEvents(new OrderReceiverListener(plugin), plugin);
    }

}
