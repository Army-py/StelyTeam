package fr.army.stelyteam.utils.network.task.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.utils.network.NetworkMessageSender;

public class AsyncStorageSender {
    
    public void sendStorage(StelyTeamPlugin plugin, Player player, String[] serverNames, Storage storage, String sourceServer){
        final NetworkMessageSender networkMessageSender = new NetworkMessageSender();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> networkMessageSender.sendStorage(player, serverNames, storage, sourceServer));
    }
}
