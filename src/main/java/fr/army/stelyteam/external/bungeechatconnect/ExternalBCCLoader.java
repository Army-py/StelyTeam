package fr.army.stelyteam.external.bungeechatconnect;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import fr.army.stelyteam.external.MessageHandler;
import fr.army.stelyteam.external.bungeechatconnect.handler.RecipientsHandler;
import fr.flowsqy.bungeechatconnect.BungeeChatConnectPlugin;

public class ExternalBCCLoader {
    
    public MessageHandler load(Plugin plugin, RecipientsHandler recipientsHandler) {
        final Plugin bccPlugin = Bukkit.getPluginManager().getPlugin("BungeeChatConnect");
        if (bccPlugin == null || !bccPlugin.isEnabled()) {
            return null;
        }
        if (!(bccPlugin instanceof BungeeChatConnectPlugin)) {
            return null;
        }
        return enable(plugin, recipientsHandler);
    }

    private MessageHandler enable(Plugin plugin, RecipientsHandler recipientsHandler) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final ExternalBCCChatListener messageListener = new ExternalBCCChatListener();
        final ExternalBCCReceiveListener receiveListener = new ExternalBCCReceiveListener(recipientsHandler);
        pluginManager.registerEvents(messageListener, plugin);
        pluginManager.registerEvents(receiveListener, plugin);
        return messageListener;
    }
}
