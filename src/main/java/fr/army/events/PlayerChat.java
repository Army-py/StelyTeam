package fr.army.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.army.App;


public class PlayerChat implements Listener {
    private String playername;

    public PlayerChat(String playername) {
        this.playername = playername;
    }

    @EventHandler
    public void PlayerChatEvent(AsyncPlayerChatEvent event){
        App.instance.getLogger().info(playername);
        if(!event.getPlayer().getName().equals(playername)){
            return;
        }

        App.instance.getLogger().info("Chat 2");
        Player player = event.getPlayer();
        PlayerInteractEvent.getHandlerList().unregister(this);
        player.sendMessage("Goood");
    }
}
