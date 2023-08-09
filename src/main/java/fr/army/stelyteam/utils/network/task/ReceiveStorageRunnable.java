package fr.army.stelyteam.utils.network.task;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.network.message.OrderReader;

public class ReceiveStorageRunnable implements Runnable {

    private final CacheManager cacheManager;
    private final byte[] storageData;

    public ReceiveStorageRunnable(StelyTeamPlugin plugin, byte[] storageData) {
        this.cacheManager = plugin.getCacheManager();
        this.storageData = storageData;
    }

    @Override
    public void run() {
        final OrderReader orderReader = new OrderReader();

        try {
            Storage storage = orderReader.read(storageData);
            cacheManager.addStorage(storage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
