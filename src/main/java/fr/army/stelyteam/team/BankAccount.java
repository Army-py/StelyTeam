package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IBankAccount;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount implements IBankAccount {

    private final Team team;
    private final Lock enableLock;
    private final Lock moneyLock;
    private boolean enable;
    private double money;

    public BankAccount(Team team, boolean enable, double money) {
        this.team = team;
        this.enableLock = new ReentrantLock();
        this.moneyLock = new ReentrantLock();
        this.enable = enable;
        this.money = money;
    }

    @Override
    public boolean isEnable() {
        enableLock.lock();
        try {
            return enable;
        } finally {
            enableLock.unlock();
        }
    }

    public void setEnable(boolean enable) {
        enableLock.lock();
        try {
            this.enable = enable;
            team.setDirty(TeamField.BANK_ACCOUNT);
        } finally {
            enableLock.unlock();
        }
    }

    @Override
    public double getMoney() {
        moneyLock.lock();
        try {
            return money;
        } finally {
            moneyLock.unlock();
        }
    }

    @Override
    public void changeAmount(double money) {
        if (money == 0) {
            return;
        }
        moneyLock.lock();
        try {
            this.money += money;
            team.setDirty(TeamField.MONEY);
        } finally {
            moneyLock.unlock();
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
