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


    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public TeamEntity getAllied() {
        return allied;
    }

    public void setAllied(TeamEntity allied) {
        this.allied = allied;
    }

    public Date getAllianceDate() {
        return allianceDate;
    }

    public void setAllianceDate(Date allianceDate) {
        this.allianceDate = allianceDate;
    }
}
