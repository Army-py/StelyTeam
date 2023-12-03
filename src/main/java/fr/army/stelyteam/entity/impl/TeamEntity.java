package fr.army.stelyteam.entity.impl;


import jakarta.persistence.*;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
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
    private UpgradesEntity upgradesEntities;

    @OneToOne(cascade = CascadeType.ALL)
    private BankAccountEntity bankAccountEntity;

    @OneToOne(optional = false)
    private MemberEntity owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teamEntity")
    private Collection<MemberEntity> memberEntities;

    @OneToMany(mappedBy = "teamEntity")
    private Collection<PermissionEntity> permissionEntities;

    @OneToMany(mappedBy = "teamEntity")
    private Collection<TeamStorageEntity> storagesEntities;

    @OneToMany(mappedBy = "team")
    private Collection<AllianceEntity> allianceEntities;

    @OneToMany(mappedBy = "allied")
    private Collection<AllianceEntity> alliesEntities;


    public int getId() {
        return id;
    }

    public TeamEntity setId(int id) {
        this.id = id;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public TeamEntity setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public TeamEntity setName(String name) {
        this.name = name;
        return this;
    }

    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    public TeamEntity setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public TeamEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public Optional<Date> getCreationDate() {
        return Optional.ofNullable(creationDate);
    }

    public TeamEntity setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public UpgradesEntity getUpgradeId() {
        return upgradesEntities;
    }

    public TeamEntity setUpgradeId(UpgradesEntity upgradeId) {
        this.upgradesEntities = upgradeId;
        return this;
    }

    public Optional<BankAccountEntity> getBankAccountEntity() {
        return Optional.ofNullable(bankAccountEntity);
    }

    public TeamEntity setBankAccountEntity(BankAccountEntity bankAccountEntity) {
        this.bankAccountEntity = bankAccountEntity;
        return this;
    }

    public Optional<Collection<MemberEntity>> getMembersEntities() {
        return Optional.ofNullable(memberEntities);
    }

    public TeamEntity setMembersEntities(Collection<MemberEntity> memberEntities) {
        this.memberEntities = memberEntities;
        return this;
    }

    public Collection<PermissionEntity> getPermissionsEntities() {
        return permissionEntities;
    }

    public TeamEntity setPermissionsEntities(Collection<PermissionEntity> permissionEntities) {
        this.permissionEntities = permissionEntities;
        return this;
    }

    public Collection<TeamStorageEntity> getStoragesEntities() {
        return storagesEntities;
    }

    public TeamEntity setStoragesEntities(Collection<TeamStorageEntity> storages) {
        this.storagesEntities = storages;
        return this;
    }

    public Collection<AllianceEntity> getAlliancesEntities() {
        return allianceEntities;
    }

    public TeamEntity setAlliancesEntities(Collection<AllianceEntity> allianceEntities) {
        this.allianceEntities = allianceEntities;
        return this;
    }

    public Collection<AllianceEntity> getAlliesEntities() {
        return alliesEntities;
    }

    public TeamEntity setAlliesEntities(Collection<AllianceEntity> allies) {
        this.alliesEntities = allies;
        return this;
    }

    public MemberEntity getOwner() {
        return owner;
    }

    public TeamEntity setOwner(MemberEntity owner) {
        this.owner = owner;
        return this;
    }
}
