package fr.army.stelyteam.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatPrefixListener implements Listener {

    private static final String PREFIX_PLACEHOLDER = "STELYTEAM_PREFIX";

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    private void onChat(AsyncPlayerChatEvent event) {
        // Get player team prefix
        final String prefix = null;
        if (prefix == null) {
            return;
        }
        event.setFormat(event.getFormat().replace(PREFIX_PLACEHOLDER, prefix));
    }

}
