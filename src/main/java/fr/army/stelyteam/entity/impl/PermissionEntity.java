package fr.army.stelyteam.entity.impl;

import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PermissionEntity implements IEntity {

    @Id
    @ManyToOne(optional = false)
    private TeamEntity teamEntity;

    @Id
    private String name;

    private int rank;


    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public PermissionEntity setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
        return this;
    }

    public String getName() {
        return name;
    }

    public PermissionEntity setName(String name) {
        this.name = name;
        return this;
    }

    public int getRank() {
        return rank;
    }

    public PermissionEntity setRank(Integer rank) {
        this.rank = rank;
        return this;
    }
}
