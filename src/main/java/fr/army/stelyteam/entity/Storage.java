package fr.army.stelyteam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Storage {
    @Id
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
