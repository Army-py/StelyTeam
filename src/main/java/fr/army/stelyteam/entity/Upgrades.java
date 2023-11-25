package fr.army.stelyteam.entity;

import jakarta.persistence.*;

@Entity
public class Upgrades {

    @Id
    @OneToOne(mappedBy = "upgradeId", optional = false)
    private Team team;

    @Column()
    private int membersAmount;

    @Column()
    private int storagesAmount;


    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getMembersAmount() {
        return membersAmount;
    }

    public void setMembersAmount(int membersAmount) {
        this.membersAmount = membersAmount;
    }

    public int getStoragesAmount() {
        return storagesAmount;
    }

    public void setStoragesAmount(int storagesAmount) {
        this.storagesAmount = storagesAmount;
    }
}
