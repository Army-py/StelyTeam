package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.Property;
import org.jetbrains.annotations.NotNull;

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

    public void loadUnsafe(@NotNull BankAccountSnapShot snapshot) {
        snapshot.unlocked().ifPresent(unlocked::loadUnsafe);
        snapshot.balance().ifPresent(balance::loadUnsafe);
    }
}
