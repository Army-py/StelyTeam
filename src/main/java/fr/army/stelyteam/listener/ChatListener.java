package fr.army.stelyteam.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.external.bungeechatconnect.handler.RecipientsHandler;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;

public class ChatListener implements Listener {
    
    private static final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private final YamlConfiguration config = plugin.getConfig();
    private final CacheManager cacheManager = plugin.getCacheManager();
    private final RecipientsHandler recipientsHandler = plugin.getRecipientsHandler();


    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onChatPre(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String usedFormat = event.getFormat()
            .replace("{TEAM}", Team.init(player).getTeamPrefix());
        event.setFormat(usedFormat);
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    private void onChat(AsyncPlayerChatEvent event) {
        if (!cacheManager.containsTeamPlayer(event.getPlayer().getUniqueId())) {
            return;
        }
        handleFormat(event);
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChatPost(AsyncPlayerChatEvent event) {
        if (!cacheManager.removeTeamPlayer(event.getPlayer().getUniqueId())) {
            return;
        }
        recipientsHandler.handle(event.getPlayer().getUniqueId(), event.getRecipients());
    }

    private void handleFormat(AsyncPlayerChatEvent event) {
        String format = config.getString("format");
        if (format == null) {
            return;
        }
        final String usedFormat = format
                .replace("{DISPLAYNAME}", "%1$s")
                .replace("{MESSAGE}", "%2$s")
                .replace("{FORMAT}", event.getFormat())
                .replace("{USERNAME}", event.getPlayer().getName())
                .replace("{TEAM}", Team.init(event.getPlayer()).getTeamPrefix());
        event.setFormat(usedFormat);
    }
}
