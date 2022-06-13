package fr.army.stelyteam.storage.network;

import fr.army.stelyteam.storage.Storage;
import fr.army.stelyteam.storage.StorageDeserializer;
import fr.army.stelyteam.storage.StorageManager;
import fr.army.stelyteam.storage.TeamField;
import fr.army.stelyteam.storage.network.packet.lifecycle.CheckStoragePacket;
import fr.army.stelyteam.storage.network.packet.lifecycle.ServerShutdownPacket;
import fr.army.stelyteam.storage.network.packet.lifecycle.ValidStoragePacket;

import java.util.*;

public class StoragePairManager {

    private final NetworkManager networkManager;
    private final String[] storageHashes;
    private final Set<String>[] servers;

    public StoragePairManager(NetworkManager networkManager, StorageDeserializer storageDeserializer) {
        this.networkManager = networkManager;
        final int size = TeamField.values().length;
        storageHashes = getStorageHashes(getStorageByField(storageDeserializer.getStorageFieldsArray()));
        servers = getServerArray(size);
    }

    private Map<TeamField, Storage> getStorageByField(StorageManager.StorageFields[] fields) {
        final Map<TeamField, Storage> storageByFieldMap = new HashMap<>();
        for (StorageManager.StorageFields storageField : fields) {
            final Storage storage = storageField.storage();
            for (TeamField field : storageField.fields()) {
                storageByFieldMap.put(field, storage);
            }
        }
        return storageByFieldMap;
    }

    private String[] getStorageHashes(Map<TeamField, Storage> storageByField) {
        final TeamField[] fields = TeamField.values();
        final String[] storageHashes = new String[fields.length];
        for (TeamField field : fields) {
            final Storage storage = storageByField.get(field);
            if (storage == null) {
                throw new RuntimeException("'" + field + "' does not specify a storage");
            }
            storageHashes[field.ordinal()] = storage.getHash();
        }
        return storageHashes;
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

    public Set<String> getServers(TeamField teamField) {
        return Collections.unmodifiableSet(servers[teamField.ordinal()]);
    }

    public void sendCheckStorage() {
        final String server = networkManager.getServerNameManager().getServerName();
        for (TeamField field : TeamField.values()) {
            networkManager.sendPacket("ALL", new CheckStoragePacket(server, field, storageHashes[field.ordinal()]));
        }
    }

    public void handleCheck(CheckStoragePacket packet) {
        final TeamField field = packet.getTeamField();
        final int fieldIndex = field.ordinal();
        final String hostedHash = storageHashes[fieldIndex];
        if (!hostedHash.equals(packet.getHash())) {
            return;
        }
        final String senderServer = packet.getServer();
        servers[fieldIndex].add(senderServer);
        final String serverName = networkManager.getServerNameManager().getServerName();
        final ValidStoragePacket validPacket = new ValidStoragePacket(serverName, field);
        networkManager.sendPacket(senderServer, validPacket);
    }

    public void handleValid(ValidStoragePacket packet) {
        servers[packet.getTeamField().ordinal()].add(packet.getServer());
    }

    public void handleServerShutdown(ServerShutdownPacket packet) {
        final String senderServer = packet.getServer();
        for (Set<String> serverSet : servers) {
            serverSet.remove(senderServer);
        }
    }

}
