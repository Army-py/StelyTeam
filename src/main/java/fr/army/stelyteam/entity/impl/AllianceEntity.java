package fr.army.stelyteam.entity.impl;


import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.Date;

@Entity
public class AllianceEntity implements IEntity {

    @Id
    @ManyToOne(optional = false)
    private TeamEntity team;

    @Id
    @ManyToOne(optional = false)
    private TeamEntity allied;

    private Date allianceDate;


    public TeamEntity getTeamEntity() {
        return team;
    }

    public AllianceEntity setTeamEntity(TeamEntity team) {
        this.team = team;
        return this;
    }

    public TeamEntity getAlliedEntity() {
        return allied;
    }

    public AllianceEntity setAlliedEntity(TeamEntity allied) {
        this.allied = allied;
        return this;
    }

    public Date getAllianceDate() {
        return allianceDate;
    }

    public AllianceEntity setAllianceDate(Date allianceDate) {
        this.allianceDate = allianceDate;
        return this;
    }
}
