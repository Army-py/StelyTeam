package fr.army.stelyteam.utils.network.task;

import org.bukkit.Bukkit;

import fr.army.stelyteam.StelyTeamPlugin;

public class AsyncStorageReceiver {
    
    public void receiveStorage(StelyTeamPlugin plugin, byte[] storageData){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new ReceiveStorageRunnable(plugin, storageData));
    }
}
