package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.Property;

public class BankAccount {

    private final Property<Boolean> unlocked;
    private final Property<Double> balance;

    public BankAccount() {
        unlocked = new Property<>();
        balance = new Property<>();
    }

    public Property<Boolean> getUnlocked() {
        return unlocked;
    }

    public Property<Double> getBalance() {
        return balance;
    }

}
