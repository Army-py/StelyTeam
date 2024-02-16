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

    public TeamStorageEntity setStorage(StorageEntity storageEntity) {
        this.storageEntity = storageEntity;
        return this;
    }

    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public TeamStorageEntity setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
        return this;
    }

    public byte[] getStorageContent() {
        return storageContent;
    }

    public TeamStorageEntity setStorageContent(byte[] storageContent) {
        this.storageContent = storageContent;
        return this;
    }
}
