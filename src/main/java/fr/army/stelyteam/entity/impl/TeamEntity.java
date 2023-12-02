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

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Optional<Date> getCreationDate() {
        return Optional.ofNullable(creationDate);
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public UpgradesEntity getUpgradeId() {
        return upgradesEntities;
    }

    public void setUpgradeId(UpgradesEntity upgradeId) {
        this.upgradesEntities = upgradeId;
    }

    public Optional<BankAccountEntity> getBankAccountEntity() {
        return Optional.ofNullable(bankAccountEntity);
    }

    public void setBankAccountEntity(BankAccountEntity bankAccountEntity) {
        this.bankAccountEntity = bankAccountEntity;
    }

    public Optional<Collection<MemberEntity>> getMembersEntities() {
        return Optional.ofNullable(memberEntities);
    }

    public void setMembersEntities(Collection<MemberEntity> memberEntities) {
        this.memberEntities = memberEntities;
    }

    public Collection<PermissionEntity> getPermissionsEntities() {
        return permissionEntities;
    }

    public void setPermissionsEntities(Collection<PermissionEntity> permissionEntities) {
        this.permissionEntities = permissionEntities;
    }

    public Collection<TeamStorageEntity> getStoragesEntities() {
        return storagesEntities;
    }

    public void setStoragesEntities(Collection<TeamStorageEntity> storages) {
        this.storagesEntities = storages;
    }

    public Collection<AllianceEntity> getAlliancesEntities() {
        return allianceEntities;
    }

    public void setAlliancesEntities(Collection<AllianceEntity> allianceEntities) {
        this.allianceEntities = allianceEntities;
    }

    public Collection<AllianceEntity> getAlliesEntities() {
        return alliesEntities;
    }

    public void setAlliesEntities(Collection<AllianceEntity> allies) {
        this.alliesEntities = allies;
    }

    public MemberEntity getOwner() {
        return owner;
    }

    public void setOwner(MemberEntity owner) {
        this.owner = owner;
    }
}
