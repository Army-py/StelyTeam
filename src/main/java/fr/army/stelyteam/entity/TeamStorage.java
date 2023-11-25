package fr.army.stelyteam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class TeamStorage {
    @Id
    @OneToOne(optional = false)
    private Storage storage;

    @Id
    @ManyToOne(optional = false)
    private Team team;

    private byte[] storageContent;


    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public byte[] getStorageContent() {
        return storageContent;
    }

    public void setStorageContent(byte[] storageContent) {
        this.storageContent = storageContent;
    }
}
