package fr.army.stelyteam.listener.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.network.ChannelRegistry;
import fr.army.stelyteam.utils.network.task.storage.AsyncStorageReceiver;
import fr.flowsqy.noqueuepluginmessage.api.event.DataReceiveEvent;

public class OrderReceiverListener implements Listener {
    
    private final StelyTeamPlugin plugin;


    public OrderReceiverListener(StelyTeamPlugin plugin) {
        this.plugin = plugin;
    }


    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onReceive(DataReceiveEvent event) {
        // System.out.println("Receive data");
        // System.out.println(event.getChannel());
        if (!ChannelRegistry.STORAGE_CHANNEL.getChannel().equals(event.getChannel())) {
            return;
        }
        // System.out.println("Channel is good");
        AsyncStorageReceiver asyncStorageReceiver = new AsyncStorageReceiver();
        asyncStorageReceiver.receiveStorage(plugin, event.getData());
    }
}
