package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.IProperty;
import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.TeamField;
import org.jetbrains.annotations.NotNull;

public class BankAccount {

    private final Property<Boolean> unlocked;
    private final Property<Double> balance;

    public BankAccount() {
        unlocked = new Property<>(TeamField.BANK_UNLOCKED);
        balance = new Property<>(TeamField.BANK_BALANCE);
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

    public void getProperties(@NotNull IProperty[] properties) {
        for (IProperty property : new IProperty[]{unlocked, balance}) {
            properties[property.getField().ordinal()] = property;
        }
    }
}
