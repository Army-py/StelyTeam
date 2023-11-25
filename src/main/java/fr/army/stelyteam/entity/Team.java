package fr.army.stelyteam.entity;


import jakarta.persistence.*;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity
public class Team {
    @Id
    @GeneratedValue()
    private int id;

    @Column(unique = true)
    private UUID uuid;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String displayName;

    private String description;

    private Date creationDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Upgrades upgradeId;

    @OneToOne(cascade = CascadeType.ALL)
    private BankAccount bankAccount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
    private Collection<Member> members;

    @OneToMany(mappedBy = "team")
    private Collection<Permission> permissions;

    @OneToMany(mappedBy = "team")
    private Collection<TeamStorage> storages;

    @OneToMany(mappedBy = "team")
    private Collection<Alliance> alliances;

    @OneToMany(mappedBy = "allied")
    private Collection<Alliance> allies;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Upgrades getUpgradeId() {
        return upgradeId;
    }

    public void setUpgradeId(Upgrades upgradeId) {
        this.upgradeId = upgradeId;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Collection<Member> getMembers() {
        return members;
    }

    public void setMembers(Collection<Member> members) {
        this.members = members;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public Collection<TeamStorage> getStorages() {
        return storages;
    }

    public void setStorages(Collection<TeamStorage> storages) {
        this.storages = storages;
    }

    public Collection<Alliance> getAlliances() {
        return alliances;
    }

    public void setAlliances(Collection<Alliance> alliances) {
        this.alliances = alliances;
    }

    public Collection<Alliance> getAllies() {
        return allies;
    }

    public void setAllies(Collection<Alliance> allies) {
        this.allies = allies;
    }
}
