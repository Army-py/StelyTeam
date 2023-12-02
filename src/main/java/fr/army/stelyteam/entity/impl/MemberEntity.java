package fr.army.stelyteam.entity.impl;


import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Entity
public class MemberEntity implements IEntity {

    @Id
    @GeneratedValue
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private PlayerEntity player;

    private Integer rank;
    private Date joiningDate;
    @ManyToOne(optional = false)
    private TeamEntity teamEntity;


    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
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

    public UUID getPlayerUuid(){
        return player.getUuid();
    }
}
