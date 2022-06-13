package fr.army.stelyteam.listener;

import fr.army.stelyteam.storage.network.ServerNameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ServerNameListener implements Listener {

    private final ServerNameManager serverNameManager;

    public ServerNameListener(ServerNameManager serverNameManager) {
        this.serverNameManager = serverNameManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent event) {
        serverNameManager.sendRequest(event.getPlayer());
    }

}
