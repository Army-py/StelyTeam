package fr.army.stelyteam.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.util.Date;

@Entity
public class Alliance {

    @Id
    @ManyToOne(optional = false)
    private Team team;

    @Id
    @ManyToOne(optional = false)
    private Team allied;

    private Date allianceDate;


    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getAllied() {
        return allied;
    }

    public void setAllied(Team allied) {
        this.allied = allied;
    }

    public Date getAllianceDate() {
        return allianceDate;
    }

    public void setAllianceDate(Date allianceDate) {
        this.allianceDate = allianceDate;
    }
}
