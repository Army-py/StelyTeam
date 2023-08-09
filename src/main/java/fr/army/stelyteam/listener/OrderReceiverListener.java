package fr.army.stelyteam.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.network.ChannelRegistry;
import fr.army.stelyteam.utils.network.task.AsyncStorageReceiver;
import fr.flowsqy.noqueuepluginmessage.api.event.DataReceiveEvent;

public class OrderReceiverListener {
    
    private final StelyTeamPlugin plugin;


    public OrderReceiverListener(StelyTeamPlugin plugin) {
        this.plugin = plugin;
    }


    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onReceive(DataReceiveEvent event) {
        if (!ChannelRegistry.STORAGE_CHANNEL.toString().equals(event.getChannel())) {
            return;
        }
        AsyncStorageReceiver asyncStorageReceiver = new AsyncStorageReceiver();
        asyncStorageReceiver.receiveStorage(plugin, event.getData());
    }
}
