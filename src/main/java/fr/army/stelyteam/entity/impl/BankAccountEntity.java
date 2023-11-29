package fr.army.stelyteam.entity.impl;

import fr.army.stelyteam.entity.IEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.util.Optional;

@Entity
public class BankAccountEntity implements IEntity {

    @Id
    @OneToOne(mappedBy = "bankAccount", optional = false)
    private TeamEntity teamEntity;
    private Boolean unlocked;
    private Double balance;


    public TeamEntity getTeamEntity() {
        return teamEntity;
    }

    public void setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
    }

    public Optional<Boolean> isUnlocked() {
        return Optional.ofNullable(unlocked);
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public Optional<Double> getBalance() {
        return Optional.ofNullable(balance);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
