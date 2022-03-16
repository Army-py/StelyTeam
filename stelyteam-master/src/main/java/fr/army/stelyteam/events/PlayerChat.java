package fr.army.stelyteam.events;


import fr.army.stelyteam.StelyTeamPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerChat implements Listener {
    private String playername;
    private ArrayList<String> teamInfos = new ArrayList<String>();
    private Integer count = 0;

    public PlayerChat(String playername) {
        this.playername = playername;
    }

    @EventHandler
    public void PlayerChatEvent(AsyncPlayerChatEvent event){
        StelyTeamPlugin.instance.getLogger().info(playername);
        if(!event.getPlayer().getName().equals(playername)){
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = event.getMessage();

        if(count == 0 && nameTeamIsTooLong(message)){
            player.sendMessage("Le nom de team est trop long");
            count--;
        }else if(count == 1 && prefixTeamIsTooLong(message)){
            player.sendMessage("Le préfixe de team est trop long");
            count--;
        }else{
            teamInfos.add(message);

            if(count == 0){
                player.sendMessage("Envoie le préfixe de team");
            }else if (count == 1){
                player.sendMessage("Team créé !");
                StelyTeamPlugin.sqlManager.insertTeam(teamInfos.get(0), teamInfos.get(1), playername);
                StelyTeamPlugin.playersCreateTeam.remove(playername);
                HandlerList.unregisterAll(this);
            }else{
                count = -1;
            }
        }
        count ++;
    }

    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > StelyTeamPlugin.config.getInt("teamNameMaxLength");
    }

    private boolean prefixTeamIsTooLong(String prefixTeam){
        Pattern pattern = Pattern.compile("§.");
        Matcher matcher = pattern.matcher(prefixTeam);
        int colors = 0;
        while (matcher.find()) {
            colors++;
        }
        return prefixTeam.length() - colors * pattern.pattern().length() > StelyTeamPlugin.config.getInt("teamPrefixMaxLength");
    }
}