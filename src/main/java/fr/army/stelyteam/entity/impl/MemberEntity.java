package fr.army.stelyteam.entity.impl;


import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Entity
public class MemberEntity extends PlayerEntity {

    private Integer rank;
    private Date joiningDate;
    @ManyToOne(optional = false)
    private TeamEntity teamEntity;


    public Optional<Integer> getRank() {
        return Optional.ofNullable(rank);
    }

    public MemberEntity setRank(Integer rank) {
        this.rank = rank;
        return this;
    }

    public Optional<Date> getJoiningDate() {
        return Optional.ofNullable(joiningDate);
    }

    public MemberEntity setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
        return this;
    }

    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public MemberEntity setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
        return this;
    }
}
