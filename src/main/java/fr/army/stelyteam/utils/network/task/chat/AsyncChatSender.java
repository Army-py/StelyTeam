package fr.army.stelyteam.utils.network.task.chat;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.network.NetworkMessageSender;

public class AsyncChatSender {
    
    public void sendMessage(StelyTeamPlugin plugin, Player player, String[] serverNames, String message,
            String sourceServer, Set<UUID> recipients) {
        final NetworkMessageSender networkMessageSender = new NetworkMessageSender();
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> networkMessageSender.sendMessage(player, serverNames, message, sourceServer, recipients));
    }
}
