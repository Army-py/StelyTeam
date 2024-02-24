package fr.army.stelyteam.utils.network.task.chat;

import org.bukkit.Bukkit;

import fr.army.stelyteam.StelyTeamPlugin;

public class AsyncChatReceiver {
    
    public void receiveChat(StelyTeamPlugin plugin, byte[] orderData){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new ReceiveChatRunnable(plugin, orderData));
    }
}
