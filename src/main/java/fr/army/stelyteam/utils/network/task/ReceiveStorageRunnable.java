package fr.army.stelyteam.utils.network.task;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.network.message.OrderReader;

public class ReceiveStorageRunnable implements Runnable {

    private final StelyTeamPlugin plugin;
    private final CacheManager cacheManager;
    private final byte[] storageData;

    public ReceiveStorageRunnable(StelyTeamPlugin plugin, byte[] storageData) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.storageData = storageData;
    }

    @Override
    public void run() {
        final OrderReader orderReader = new OrderReader();

        try {
            Storage storage = orderReader.read(storageData);
            if (storage.getOpenedServerName() != null && storage.getOpenedServerName().equals(plugin.getCurrentServerName())){
                return;
            }

            cacheManager.addStorage(storage);
            // System.out.println("Storage received");
            // System.out.println(storage.getStorageId());
            // System.out.println(storage.getTeamUuid().toString());
            // System.out.println(storage.getOpenedServerName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
