package fr.army.stelyteam.entity.impl;

import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class BankAccountEntity implements IEntity {

    @Id
    @OneToOne(mappedBy = "bankAccount", optional = false)
    private TeamEntity teamEntity;
    private boolean unlocked;
    private double balance;


    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public void setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
