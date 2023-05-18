package fr.army.stelyteam.team;

import java.util.UUID;

import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;

public class Storage {
    private UUID teamUuid;
    private Integer storageId;
    private Inventory inventoryInstance;
    private byte[] storageContent;

    public Storage(UUID teamUuid, Integer storageId, Inventory inventoryInstance, byte[] storageContent){
        this.teamUuid = teamUuid;
        this.storageId = storageId;
        this.inventoryInstance = inventoryInstance;
        this.storageContent = storageContent;
    }


    public void saveStorageToCache(){
        StelyTeamPlugin.getPlugin().getCacheManager().saveStorage(this);
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
}
