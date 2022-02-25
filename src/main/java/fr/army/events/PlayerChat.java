package fr.army.events;


import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.army.App;


public class PlayerChat implements Listener {
    private String playername;
    private ArrayList<String> teamInfos = new ArrayList<String>();
    private Integer count = 0;

    public PlayerChat(String playername) {
        this.playername = playername;
    }

    @EventHandler
    public void PlayerChatEvent(AsyncPlayerChatEvent event){
        App.instance.getLogger().info(playername);
        if(!event.getPlayer().getName().equals(playername)){
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        String message = event.getMessage();
        teamInfos.add(message);
        
        player.sendMessage("Goood");
        count ++;
        if(count == 2){
            createTeam();
            App.playersCreateTeam.remove(App.playersCreateTeam.indexOf(playername));
            HandlerList.unregisterAll(this);
        }
    }


    private void createTeam(){
        App.sqlManager.insertTeam(teamInfos.get(0), teamInfos.get(1), playername);
    }
}
