package fr.army.stelyteam.entity.impl;


import jakarta.persistence.*;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity
public class TeamEntity {
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
    private UpgradesEntity upgradeId;

    @OneToOne(cascade = CascadeType.ALL)
    private BankAccountEntity bankAccountEntity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teamEntity")
    private Collection<MemberEntity> memberEntities;

    @OneToMany(mappedBy = "teamEntity")
    private Collection<PermissionEntity> permissionEntities;

    @OneToMany(mappedBy = "teamEntity")
    private Collection<TeamStorageEntity> storages;

    @OneToMany(mappedBy = "team")
    private Collection<AllianceEntity> allianceEntities;

    @OneToMany(mappedBy = "allied")
    private Collection<AllianceEntity> allies;


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

    public UpgradesEntity getUpgradeId() {
        return upgradeId;
    }

    public void setUpgradeId(UpgradesEntity upgradeId) {
        this.upgradeId = upgradeId;
    }

    public BankAccountEntity getBankAccount() {
        return bankAccountEntity;
    }

    public void setBankAccount(BankAccountEntity bankAccountEntity) {
        this.bankAccountEntity = bankAccountEntity;
    }

    public Collection<MemberEntity> getMembers() {
        return memberEntities;
    }

    public void setMembers(Collection<MemberEntity> memberEntities) {
        this.memberEntities = memberEntities;
    }

    public Collection<PermissionEntity> getPermissions() {
        return permissionEntities;
    }

    public void setPermissions(Collection<PermissionEntity> permissionEntities) {
        this.permissionEntities = permissionEntities;
    }

    public Collection<TeamStorageEntity> getStorages() {
        return storages;
    }

    public void setStorages(Collection<TeamStorageEntity> storages) {
        this.storages = storages;
    }

    public Collection<AllianceEntity> getAlliances() {
        return allianceEntities;
    }

    public void setAlliances(Collection<AllianceEntity> allianceEntities) {
        this.allianceEntities = allianceEntities;
    }

    public Collection<AllianceEntity> getAllies() {
        return allies;
    }

    public void setAllies(Collection<AllianceEntity> allies) {
        this.allies = allies;
    }
}
