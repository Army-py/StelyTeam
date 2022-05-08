package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IBankAccount;
import fr.army.stelyteam.storage.TeamField;

import java.util.concurrent.locks.ReentrantLock;

public class BankAccount implements IBankAccount {

    private final Team team;
    private final ReentrantLock lock;
    private boolean enable;
    private double money;

    BankAccount(Team team, ReentrantLock lock, boolean enable, double money) {
        this.team = team;
        this.lock = lock;
        this.enable = enable;
        this.money = money;
    }

    @Override
    public boolean isEnable() {
        lock.lock();
        try {
            return enable;
        } finally {
            lock.unlock();
        }
    }

    public void setEnable(boolean enable) {
        lock.lock();
        try {
            this.enable = enable;
            team.setDirty(TeamField.BANK_ACCOUNT);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getMoney() {
        lock.lock();
        try {
            return money;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void changeAmount(double money) {
        if (money == 0) {
            return;
        }
        lock.lock();
        try {
            this.money += money;
            team.setDirty(TeamField.MONEY);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void incrementAmount(double money) {
        changeAmount(money);
    }

    @Override
    public void decrementAmount(double money) {
        changeAmount(-money);
    }

}
