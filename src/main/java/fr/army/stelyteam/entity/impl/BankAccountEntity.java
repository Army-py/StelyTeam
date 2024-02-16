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

    public BankAccountEntity setTeamEntity(TeamEntity team) {
        this.teamEntity = team;
        return this;
    }

    public Optional<Boolean> isUnlocked() {
        return Optional.ofNullable(unlocked);
    }

    public BankAccountEntity setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
        return this;
    }

    public Optional<Double> getBalance() {
        return Optional.ofNullable(balance);
    }

    public BankAccountEntity setBalance(Double balance) {
        this.balance = balance;
        return this;
    }
}
