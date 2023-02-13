package fr.army.stelyteam.utils;

import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;

public class Storage {
    private Team team;
    private Integer storageId;
    private Inventory inventoryInstance;
    private byte[] storageContent;

    public Storage(Team team, Integer storageId, Inventory inventoryInstance, byte[] storageContent){
        this.team = team;
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

    public Team getTeam() {
        return team;
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

    public void setTeam(Team team) {
        this.team = team;
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
