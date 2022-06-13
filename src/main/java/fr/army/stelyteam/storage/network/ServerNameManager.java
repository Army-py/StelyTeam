package fr.army.stelyteam.storage.network;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.listener.ServerNameListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ServerNameManager {

    private final NetworkManager networkManager;
    private ServerNameListener listener;
    private String serverName;

    public ServerNameManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void load(StelyTeamPlugin plugin) {
        this.listener = new ServerNameListener(this);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void unload() {
        unregisterListener();
    }

    public String getServerName() {
        return serverName;
    }

    /**
     * Send a request to BungeeCord to get the current server name
     *
     * @param player The player that will be used as a bridge to send the request
     */
    public void sendRequest(Player player) {
        // TODO Send a getServer request to BungeeCord
    }

    public void handleResponse() {
        // TODO affect the server name to the local variable

        unregisterListener();
    }

    /**
     * Unregister the current working listener
     */
    private void unregisterListener() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
            listener = null;
        }
    }

}
