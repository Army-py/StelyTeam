package fr.army.stelyteam.team;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.network.task.AsyncStorageSender;

public class Storage {
    private UUID teamUuid;
    private Integer storageId;
    private Inventory inventoryInstance;
    private byte[] storageContent;
    private String openedServerName;

    public Storage(@NotNull UUID teamUuid, @NotNull Integer storageId, @Nullable Inventory inventoryInstance,
            @NotNull byte[] storageContent, @Nullable String openedServerName) {
        this.teamUuid = teamUuid;
        this.storageId = storageId;
        this.inventoryInstance = inventoryInstance;
        this.storageContent = storageContent;
        this.openedServerName = openedServerName;
    }


    public void saveStorageToCache(StelyTeamPlugin plugin, Player player){
        StelyTeamPlugin.getPlugin().getCacheManager().saveStorage(this);

        final AsyncStorageSender storageSender = new AsyncStorageSender();
        final String[] serverNames = plugin.getServerNames();
        storageSender.sendStorage(plugin, player, serverNames, this, openedServerName);
    }

    public void saveStorageToDatabase(){
        StelyTeamPlugin.getPlugin().getDatabaseManager().saveStorage(this);
    }

    public UUID getTeamUuid() {
        return teamUuid;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public Inventory getInventoryInstance() {
        return inventoryInstance;
    }

    public byte[] getStorageContent() {
        return storageContent;
    }

    @Nullable
    public String getOpenedServerName() {
        return openedServerName;
    }

    public void setTeamUuid(UUID teamUuid) {
        this.teamUuid = teamUuid;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public void setStorageInstance(Inventory inventoryInstance) {
        this.inventoryInstance = inventoryInstance;
    }

    public void setStorageContent(byte[] storageContent) {
        this.storageContent = storageContent;
    }

    public void setOpenedServerName(String openedServerName) {
        this.openedServerName = openedServerName;
    }
}
