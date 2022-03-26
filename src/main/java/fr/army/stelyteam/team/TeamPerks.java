package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IBankAccount;
import fr.army.stelyteam.api.ITeamPerks;
import fr.army.stelyteam.api.LazyLocation;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TeamPerks implements ITeamPerks {

    private final Team team;
    private final Lock lock;
    private final BankAccount bankAccount;
    private int level;
    private LazyLocation home;

    public TeamPerks(Team team, int level, boolean bankAccount, double money, LazyLocation home) {
        this.team = team;
        this.lock = new ReentrantLock();
        this.bankAccount = new BankAccount(team, bankAccount, money);
        this.level = level;
        this.home = home;
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
            if (this.level == level) {
                return;
            }
            this.level = level;
            team.setDirty(TeamField.LEVEL);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IBankAccount getBankAccount() {
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
            if (Objects.equals(this.home, home)) {
                return;
            }
            this.home = home == null ? null : home.clone();
            team.setDirty(TeamField.HOME);
        } finally {
            lock.unlock();
        }
    }
}
