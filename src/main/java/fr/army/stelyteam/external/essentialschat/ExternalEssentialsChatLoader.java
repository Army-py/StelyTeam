package fr.army.stelyteam.external.essentialschat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import com.earth2me.essentials.chat.EssentialsChat;

public class ExternalEssentialsChatLoader {
    
    public void load(@NotNull Plugin plugin) {
        final Plugin essentialChatPlugin = Bukkit.getPluginManager().getPlugin("EssentialsChat");
        if (essentialChatPlugin == null || !essentialChatPlugin.isEnabled()) {
            return;
        }
        if (!(essentialChatPlugin instanceof EssentialsChat)) {
            return;
        }
        enable(essentialChatPlugin);
    }

    private void enable(@NotNull Plugin plugin) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final ExternalEssentialsSpyListener spyListener = new ExternalEssentialsSpyListener();
        pluginManager.registerEvents(spyListener, plugin);
    }

}
