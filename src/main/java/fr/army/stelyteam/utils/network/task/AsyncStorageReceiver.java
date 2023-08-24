package fr.army.stelyteam.utils.network.task;

import org.bukkit.Bukkit;

import fr.army.stelyteam.StelyTeamPlugin;

public class AsyncStorageReceiver {
    
    public void receiveOrder(StelyTeamPlugin plugin, byte[] orderData){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new ReceiveStorageRunnable(plugin, orderData));
    }
}
