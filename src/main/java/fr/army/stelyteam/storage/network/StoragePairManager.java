package fr.army.stelyteam.storage.network;

import fr.army.stelyteam.storage.TeamField;
import fr.army.stelyteam.storage.network.packet.lifecycle.CheckStoragePacket;
import fr.army.stelyteam.storage.network.packet.lifecycle.ServerShutdownPacket;
import fr.army.stelyteam.storage.network.packet.lifecycle.ValidStoragePacket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StoragePairManager {

    private final NetworkManager networkManager;
    private final String[] storages;
    private final Set<String>[] servers;

    public StoragePairManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
        storages = getStorages();
        servers = getServerArray(TeamField.values().length);
    }

    private String[] getStorages() {
        final String[] storages = new String[TeamField.values().length];
        // TODO Fill with storage hashes
        Arrays.fill(storages, "TODO");
        return storages;
    }

    @SuppressWarnings("unchecked")
    private Set<String>[] getServerArray(int size) {
        final Set<String>[] servers = new Set[size];
        // Fill the server array with empty set
        for (int index = 0; index < size; index++) {
            servers[index] = new HashSet<>();
        }
        return servers;
    }

    public void handleCheck(CheckStoragePacket packet) {
        final TeamField field = packet.getTeamField();
        final int fieldIndex = field.ordinal();
        final String hostedHash = storages[fieldIndex];
        if (!hostedHash.equals(packet.getHash())) {
            return;
        }
        final String senderServer = packet.getServer();
        servers[fieldIndex].add(senderServer);
        final ValidStoragePacket validPacket = new ValidStoragePacket(networkManager.getServer(), field);
        networkManager.sendPacket(senderServer, validPacket);
    }

    public void handleValid(ValidStoragePacket packet) {
        servers[packet.getTeamField().ordinal()].add(packet.getServer());
    }

    public void handleServerShutdown(ServerShutdownPacket packet) {
        for (Set<String> serverSet : servers) {
            serverSet.remove(packet.getServer());
        }
    }

}
