package fr.army.stelyteam.team;

import fr.army.stelyteam.api.LazyLocation;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class TeamBuilder {

    private UUID uuid;
    private String commandId;
    private String prefix;
    private String suffix;
    private UUID creator;
    private Date creationDate;
    private int level;
    private boolean bankAccount;
    private double money;
    private LazyLocation home;
    private Map<UUID, Integer> players;

    public UUID getId() {
        return uuid;
    }

    public TeamBuilder setId(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getCommandId() {
        return commandId;
    }

    public TeamBuilder setCommandId(String commandId) {
        this.commandId = commandId;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public TeamBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getSuffix() {
        return suffix;
    }

    public TeamBuilder setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public UUID getCreator() {
        return creator;
    }

    public TeamBuilder setCreator(UUID creator) {
        this.creator = creator;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public TeamBuilder setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public TeamBuilder setLevel(int level) {
        this.level = level;
        return this;
    }

    public boolean isBankAccount() {
        return bankAccount;
    }

    public TeamBuilder setBankAccount(boolean bankAccount) {
        this.bankAccount = bankAccount;
        return this;
    }

    public double getMoney() {
        return money;
    }

    public TeamBuilder setMoney(double money) {
        this.money = money;
        return this;
    }

    public LazyLocation getHome() {
        return home;
    }

    public TeamBuilder setHome(LazyLocation home) {
        this.home = home;
        return this;
    }

    public Map<UUID, Integer> getPlayers() {
        return players;
    }

    public TeamBuilder setPlayers(Map<UUID, Integer> players) {
        this.players = players;
        return this;
    }

    public Team create() {
        if (uuid == null) {
            throw new IllegalArgumentException("Can't create a team that have no uuid");
        }

        return new Team(
                uuid,
                commandId,
                prefix,
                suffix,
                creator,
                creationDate,
                level,
                bankAccount,
                money,
                home,
                players
        );
    }

}
