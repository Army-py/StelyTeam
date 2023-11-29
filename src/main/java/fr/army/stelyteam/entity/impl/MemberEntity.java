package fr.army.stelyteam.entity.impl;


import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Entity
public class MemberEntity implements IEntity {

    @Id
    @GeneratedValue
    private int id;

    private UUID playerUuid;
    private Integer rank;
    private Date joiningDate;
    @ManyToOne(optional = false)
    private TeamEntity teamEntity;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public Optional<Integer> getRank() {
        return Optional.ofNullable(rank);
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Optional<Date> getJoiningDate() {
        return Optional.ofNullable(joiningDate);
    }

    public void setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
    }

    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public void setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
    }
}
