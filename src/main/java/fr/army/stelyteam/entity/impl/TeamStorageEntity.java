package fr.army.stelyteam.entity.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class TeamStorageEntity {
    @Id
    @OneToOne(optional = false)
    private StorageEntity storageEntity;

    @Id
    @ManyToOne(optional = false)
    private TeamEntity teamEntity;

    private byte[] storageContent;


    public StorageEntity getStorage() {
        return storageEntity;
    }

    public void setStorage(StorageEntity storageEntity) {
        this.storageEntity = storageEntity;
    }

    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public void setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
    }

    public byte[] getStorageContent() {
        return storageContent;
    }

    public void setStorageContent(byte[] storageContent) {
        this.storageContent = storageContent;
    }
}
