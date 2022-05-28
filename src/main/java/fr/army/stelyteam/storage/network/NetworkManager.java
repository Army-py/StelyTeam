package fr.army.stelyteam.storage.network;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.storage.PlayerTeamTracker;
import fr.army.stelyteam.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NetworkManager implements PluginMessageListener {

    // TODO Handle field by field pairing

    private final StelyTeamPlugin plugin;
    private final Object lock;
    private boolean loaded;

    public NetworkManager(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        lock = new Object();
    }

    public void load() {
        synchronized (lock) {
            if (loaded) {
                throw new IllegalStateException("The network manager is already loaded");
            }
            final Messenger messenger = Bukkit.getMessenger();
            messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this);
            messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
            loaded = true;
        }
    }

    public void unload() {
        synchronized (lock) {
            if (!loaded) {
                throw new IllegalStateException("The network manager is not loaded");
            }
            final Messenger messenger = Bukkit.getMessenger();
            messenger.unregisterIncomingPluginChannel(plugin, "BungeeCord", this);
            messenger.unregisterOutgoingPluginChannel(plugin, "BungeeCord");
            loaded = false;
        }
    }

    public void sendMessage(byte[] msgBytes) {
        synchronized (lock) {
            if (!loaded) {
                throw new IllegalStateException("The network manager is not loaded");
            }
        }
        final ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
        final DataOutputStream dataStream = new DataOutputStream(arrayStream);
        try {
            dataStream.writeUTF("Forward");
            dataStream.writeUTF("ALL");
            dataStream.writeUTF("StelyTeam");

            dataStream.writeShort(msgBytes.length);
            dataStream.write(msgBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", arrayStream.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        final ByteArrayInputStream arrayStream = new ByteArrayInputStream(message);
        final DataInputStream dataStream = new DataInputStream(arrayStream);

        final byte[] msgBytes;
        try {
            final String subChannel = dataStream.readUTF();
            if (!subChannel.equals("StelyTeam")) {
                return;
            }
            final short msgLength = dataStream.readShort();
            msgBytes = new byte[msgLength];
            dataStream.readFully(msgBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO Handle msgBytes
    }


    /**
     * Send team field changes through the network
     *
     * @param fieldValues The field values to update
     */
    public void sendSave(List<Storage.FieldValues> fieldValues) {
    }

    /**
     * Send players changes through the network
     *
     * @param changes The player associations to change
     */
    public void sendPlayers(Map<UUID, PlayerTeamTracker.PlayerChange> changes) {

    }

    /**
     * Send delete team action through the network
     *
     * @param teamId The team to delete
     */
    public void sendDelete(UUID teamId) {
    }
}
