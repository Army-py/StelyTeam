package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.*;
import fr.army.stelyteam.entity.impl.BankAccountEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BankAccount {

    private final Property<Boolean> unlocked;
    private final Property<Double> balance;

    public BankAccount() {
        unlocked = new Property<>(SaveField.BANK_UNLOCKED);
        balance = new Property<>(SaveField.BANK_BALANCE);
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

    public void loadUnsafe(@NotNull BankAccountEntity entity) {
        entity.isUnlocked().ifPresent(unlocked::loadUnsafe);
        entity.getBalance().ifPresent(balance::loadUnsafe);
    }

    public void save(@NotNull PropertiesHolder holder, @NotNull List<SaveProperty<?>> values) {
        unlocked.save(holder, values);
        balance.save(holder, values);
    }

    public void getProperties(@NotNull IProperty[] properties) {
        for (IProperty property : new IProperty[]{unlocked, balance}) {
            properties[property.getField().ordinal()] = property;
        }
    }
}
