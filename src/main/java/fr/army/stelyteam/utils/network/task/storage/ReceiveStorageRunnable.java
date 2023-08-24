package fr.army.stelyteam.utils.network.task.storage;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.network.order.Order;
import fr.army.stelyteam.utils.network.order.OrderReader;
import fr.army.stelyteam.utils.network.storage.StorageReader;

public class ReceiveStorageRunnable implements Runnable {

    private final StelyTeamPlugin plugin;
    private final CacheManager cacheManager;
    private final byte[] orderData;

    public ReceiveStorageRunnable(StelyTeamPlugin plugin, byte[] orderData) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.orderData = orderData;
    }

    @Override
    public void run() {
        final OrderReader orderReader = new OrderReader();
        final StorageReader storageReader = new StorageReader();

        try {
            final Order order = orderReader.read(orderData);
            final Storage storage = storageReader.read(order.data());
            if (order.sourceServerName().equals(plugin.getCurrentServerName())) {
                return;
            }
            if (storage.getOpenedServerName() != null && storage.getOpenedServerName().equals(plugin.getCurrentServerName())){
                return;
            }

            cacheManager.saveStorage(storage);
            System.out.println("Storage received");
            System.out.println(storage.getStorageId());
            System.out.println(storage.getTeamUuid().toString());
            System.out.println(storage.getOpenedServerName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
