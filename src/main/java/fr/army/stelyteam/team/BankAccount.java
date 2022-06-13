package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IBankAccount;
import fr.army.stelyteam.storage.TeamField;

import java.util.concurrent.locks.ReentrantLock;

public class BankAccount implements IBankAccount {

    private final Team team;
    private final ReentrantLock lock;
    private final Unsafe unsafe;
    private boolean enable;
    private double money;

    public BankAccount(Team team, ReentrantLock lock) {
        this.team = team;
        this.lock = lock;
        this.unsafe = new Unsafe();
    }

    BankAccount(Team team, ReentrantLock lock, boolean enable, double money) {
        this.team = team;
        this.lock = lock;
        this.unsafe = new Unsafe();
        this.enable = enable;
        this.money = money;
    }

    public Unsafe getUnsafe() {
        return unsafe;
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
            if (unsafe.setEnable(enable)) {
                team.setDirty(TeamField.BANK_ACCOUNT);
            }
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

    public class Unsafe {

        public boolean setEnable(boolean enable) {
            lock.lock();
            try {
                if (BankAccount.this.enable == enable) {
                    return false;
                }
                BankAccount.this.enable = enable;
                return true;
            } finally {
                lock.unlock();
            }
        }

        public void setMoney(double money) {
            lock.lock();
            try {
                BankAccount.this.money = money;
            } finally {
                lock.unlock();
            }
        }

    }

}
