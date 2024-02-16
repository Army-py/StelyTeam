package fr.army.stelyteam.entity.impl;

import jakarta.persistence.*;

@Entity
public class UpgradesEntity {

    @Id
    @OneToOne(mappedBy = "upgradeId", optional = false)
    private TeamEntity teamEntity;

    @Column()
    private int membersAmount;

    @Column()
    private int storagesAmount;


    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public void setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
    }

    public int getMembersAmount() {
        return membersAmount;
    }

    public UpgradesEntity setMembersAmount(Integer membersAmount) {
        this.membersAmount = membersAmount;
        return this;
    }

    public int getStoragesAmount() {
        return storagesAmount;
    }

    public UpgradesEntity setStoragesAmount(Integer storagesAmount) {
        this.storagesAmount = storagesAmount;
        return this;
    }
}
