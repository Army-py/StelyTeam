package fr.army.stelyteam.entity.impl;


import fr.army.stelyteam.cache.SetProperty;
import fr.army.stelyteam.repository.impl.TeamRepository;
import fr.army.stelyteam.team.*;
import jakarta.persistence.*;
import org.bukkit.entity.Player;

import java.util.*;

@Entity
public class TeamEntity {
    @Id
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


    public static class Builder{

        private final TeamRepository teamRepository;
        private String name = null;
        private String displayName = null;
        private String description = null;
        private Date creationDate = null;
        private UUID ownerUuid = null;
        private String ownerName = null;
        private Upgrades upgrades = null;
        private BankAccount bankAccount = null;
        private SetProperty<UUID, Member> members = null;
        private SetProperty<String, Permission> permissions = null;
        private SetProperty<UUID, Alliance> alliances = null;
        private SetProperty<Integer, Storage> storages = null;

        public Builder(TeamRepository teamRepository){
            this.teamRepository = teamRepository;
        }

        public Builder setName(String name){
            this.name = name;
            return this;
        }

        public Builder setDisplayName(String displayName){
            this.displayName = displayName;
            return this;
        }

        public Builder setDescription(String description){
            this.description = description;
            return this;
        }

        public Builder setCreationDate(Date creationDate){
            this.creationDate = creationDate;
            return this;
        }

        public Builder setOwner(Player owner){
            this.ownerUuid = owner.getUniqueId();
            this.ownerName = owner.getName();
            return this;
        }

        public Builder setOwner(UUID ownerUuid, String ownerName){
            this.ownerUuid = ownerUuid;
            this.ownerName = ownerName;
            return this;
        }

        public Builder setOwner(Member owner){
            this.ownerUuid = owner.getId();
            this.ownerName = owner.getName().get();
            return this;
        }

        public Builder setUpgrades(Upgrades upgrades){
            this.upgrades = upgrades;
            return this;
        }

        public Builder setBankAccount(BankAccount bankAccount){
            this.bankAccount = bankAccount;
            return this;
        }

        public Builder setMembers(SetProperty<UUID, Member> members){
            this.members = members;
            return this;
        }

        public Builder setPermissions(SetProperty<String, Permission> permissions){
            this.permissions = permissions;
            return this;
        }

        public Builder setAlliances(SetProperty<UUID, Alliance> alliances){
            this.alliances = alliances;
            return this;
        }

        public Builder setStorages(SetProperty<Integer, Storage> storages){
            this.storages = storages;
            return this;
        }

        public TeamEntity build(){
            final TeamEntity team = new TeamEntity()
                .setUuid(UUID.randomUUID())
                .setName(name)
                .setDisplayName(displayName)
                .setDescription(description)
                .setCreationDate(creationDate);

            final MemberEntity member = new MemberEntity()
                .setRank(0)
                .setJoiningDate(new Date());
            member.setUuid(ownerUuid);
            member.setName(ownerName);
            member.setTeamEntity(team);
            team.setOwner(member);

            if (upgrades != null)
                team.setUpgradeId(
                        new UpgradesEntity()
                                .setMembersAmount(upgrades.getMembers().get())
                                .setStoragesAmount(upgrades.getStorage().get())
                        );

            if (bankAccount != null)
                team.setBankAccountEntity(
                        new BankAccountEntity()
                                .setBalance(bankAccount.getBalance().get())
                                .setUnlocked(bankAccount.getUnlocked().get())
                        );

            if (members != null){
                for (Member me : members.toArray(new Member[0])){
                    final MemberEntity memberEntity = new MemberEntity()
                        .setRank(me.getRank().get())
                        .setJoiningDate(me.getJoiningDate().get());
                    memberEntity.setUuid(me.getId());
                    memberEntity.setName(me.getName().get());
                    memberEntity.setTeamEntity(team);
                    team.getMembersEntities().ifPresent(membersEntities -> membersEntities.add(memberEntity));
                }
            }

            if (permissions != null){
                for (Permission p : permissions.toArray(new Permission[0])){
                    final PermissionEntity permissionEntity = new PermissionEntity()
                        .setName(p.getName())
                        .setRank(p.getTeamRank().get());
                    permissionEntity.setTeamEntity(team);
                    team.getPermissionsEntities().add(permissionEntity);
                }
            }

            if (alliances != null){
                for (Alliance a : alliances.toArray(new Alliance[0])){
                    final AllianceEntity allianceEntity = new AllianceEntity()
                        .setTeamEntity(team)
                        .setAlliedEntity(teamRepository.getReference(a.getTeamUuid()));
                    team.getAlliancesEntities().add(allianceEntity);
                }
            }

            if (storages != null){
                for (Storage s : storages.toArray(new Storage[0])){
                    final TeamStorageEntity storageEntity = new TeamStorageEntity()
                        .setTeamEntity(team)
                        .setStorage(new StorageEntity().setId(s.getStorageId()))  // TODO: check if this is correct and works
                        .setStorageContent(s.getStorageContent());
                    team.getStoragesEntities().add(storageEntity);
                }
            }

            return team;
        }

        public static TeamEntity fromTeam(Team team, TeamRepository teamRepository){
            return new Builder(teamRepository)
                .setName(team.getName().get())
                .setDisplayName(team.getDisplayName().get())
                .setDescription(team.getDescription().get())
                .setCreationDate(team.getCreationDate().get())
                .setOwner(Objects.requireNonNull(team.getOwner().get()))
                .setUpgrades(team.getUpgrades())
                .setBankAccount(team.getBankAccount())
                .setMembers(team.getMembers())
                .setPermissions(team.getPermissions())
                .setAlliances(team.getAlliances())
                .setStorages(team.getStorages())
                .build();
        }
    }

}
