package fr.army.stelyteam.entity.impl;

import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class StorageEntity implements IEntity {
    @Id
    @GeneratedValue
    private int id;

    public StorageEntity setId(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }
}
