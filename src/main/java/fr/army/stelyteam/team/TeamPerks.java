package fr.army.stelyteam.team;

import fr.army.stelyteam.api.ITeamPerks;
import fr.army.stelyteam.api.LazyLocation;
import fr.army.stelyteam.storage.TeamField;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class TeamPerks implements ITeamPerks {

    private final Team team;
    private final ReentrantLock lock;
    private final BankAccount bankAccount;
    private final Unsafe unsafe;
    private int level;
    private LazyLocation home;

    public TeamPerks(Team team, ReentrantLock lock) {
        this.team = team;
        this.lock = lock;
        this.unsafe = new Unsafe();
        this.bankAccount = new BankAccount(team, lock);
    }

    public TeamPerks(Team team, ReentrantLock lock, int level, boolean bankAccount, double money, LazyLocation home) {
        this.team = team;
        this.lock = lock;
        this.bankAccount = new BankAccount(team, lock, bankAccount, money);
        this.unsafe = new Unsafe();
        this.level = level;
        this.home = home;
    }

    public Unsafe getUnsafe() {
        return unsafe;
    }

    @Override
    public int getLevel() {
        lock.lock();
        try {
            return level;
        } finally {
            lock.unlock();
        }
    }

    public void setLevel(int level) {
        lock.lock();
        try {
            if (getUnsafe().setLevel(level)) {
                team.setDirty(TeamField.LEVEL);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public BankAccount getBankAccount() {
        return bankAccount;
    }

    @Override
    public LazyLocation getHome() {
        lock.lock();
        try {
            return home == null ? null : home.clone();
        } finally {
            lock.unlock();
        }
    }

    public void setHome(LazyLocation home) {
        lock.lock();
        try {
            if (unsafe.setHome(home)) {
                team.setDirty(TeamField.HOME);
            }
        } finally {
            lock.unlock();
        }
    }

    public class Unsafe {

        public boolean setLevel(int level) {
            lock.lock();
            try {
                if (TeamPerks.this.level == level) {
                    return false;
                }
                TeamPerks.this.level = level;
                return true;
            } finally {
                lock.unlock();
            }
        }

        public boolean setHome(LazyLocation home) {
            lock.lock();
            try {
                if (Objects.equals(TeamPerks.this.home, home)) {
                    return false;
                }
                TeamPerks.this.home = home == null ? null : home.clone();
                return true;
            } finally {
                lock.unlock();
            }
        }

    }

}
