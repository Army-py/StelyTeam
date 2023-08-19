package fr.army.stelyteam.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;

public class ChatPrefixListener implements Listener {

    public static final String PREFIX_PLACEHOLDER = "{STELYTEAM_PREFIX}";
    
    private StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private ColorsBuilder colorBuilder = plugin.getColorsBuilder();

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Team team = Team.getFromCache(player);
        String prefix = "";

        if (team != null) {
            prefix = colorBuilder.replaceColor(team.getTeamPrefix());
        }

        // ChatColor.translateAlternateColorCodes(0, prefix)
        event.setFormat(event.getFormat().replace(PREFIX_PLACEHOLDER, prefix));
    }

}
