package fr.army.stelyteam.team;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.impl.AdminMenu;
import fr.army.stelyteam.menu.impl.EditAlliancesMenu;
import fr.army.stelyteam.menu.impl.EditMembersMenu;
import fr.army.stelyteam.menu.impl.ManageMenu;
import fr.army.stelyteam.menu.impl.MemberMenu;
import fr.army.stelyteam.menu.impl.MembersMenu;
import fr.army.stelyteam.menu.impl.PermissionsMenu;
import fr.army.stelyteam.menu.impl.StorageDirectoryMenu;
import fr.army.stelyteam.menu.impl.UpgradeMembersMenu;
import fr.army.stelyteam.utils.manager.CacheManager;

public class Team {

    private static StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private YamlConfiguration config = plugin.getConfig();
    private static CacheManager cacheManager = StelyTeamPlugin.getPlugin().getCacheManager();

    private final UUID teamUuid;
    private String teamName;
    private String teamPrefix = null;
    private String teamDescription = config.getString("team.defaultDescription");
    private Double teamMoney = 0.0;
    private String creationDate = getCurrentDate();
    private Integer improvLvlMembers = 0;
    private Integer teamStorageLvl = 0;
    private Boolean unlockedTeamBank = false;
    private String teamOwnerName;
    private Set<Member> teamMembers = new HashSet<>();
    private Set<Permission> teamPermissions = new HashSet<>();
    private Set<Alliance> teamAlliances = new HashSet<>();
    private Map<Integer, Storage> teamStorages;

    // Existing team
    public Team(UUID teamUuid, String teamName, String teamPrefix, String teamDescription, double teamMoney, String creationDate, int improvLvlMembers, int teamStorageLvl, boolean unlockedTeamBank, String teamOwnerName){
        this.teamUuid = teamUuid;
        this.teamName = teamName;
        this.teamPrefix = teamPrefix;
        this.teamDescription = teamDescription;
        this.teamMoney = teamMoney;
        this.creationDate = creationDate;
        this.improvLvlMembers = improvLvlMembers;
        this.teamStorageLvl = teamStorageLvl;
        this.unlockedTeamBank = unlockedTeamBank;
        this.teamOwnerName = teamOwnerName;

        this.teamMembers = plugin.getDatabaseManager().getTeamMembers(teamUuid);
        this.teamPermissions = plugin.getDatabaseManager().getTeamAssignement(teamUuid);
        this.teamAlliances = plugin.getDatabaseManager().getTeamAlliances(teamUuid);
        this.teamStorages = plugin.getDatabaseManager().getTeamStorages(teamUuid);
    }

    // New team
    public Team(String teamName, String teamOwnerName){
        this.teamUuid = UUID.randomUUID();
        this.teamName = teamName;
        this.teamOwnerName = teamOwnerName;
    }


    public void createTeam(){
        plugin.getDatabaseManager().insertTeam(this);
        cacheManager.addTeam(Team.init(teamUuid));
    }

    public static Team init(String teamName){
        return cacheManager.getTeamByName(teamName) == null 
            ? plugin.getDatabaseManager().getTeamFromTeamName(teamName) 
            : cacheManager.getTeamByName(teamName);
    }

    public static Team init(Player player){
        return cacheManager.getTeamByPlayerName(player.getName()) == null 
            ? plugin.getDatabaseManager().getTeamFromPlayerName(player.getName()) 
            : cacheManager.getTeamByPlayerName(player.getName());
    }

    public static Team initFromPlayerName(String playerName){
        return cacheManager.getTeamByPlayerName(playerName) == null 
            ? plugin.getDatabaseManager().getTeamFromPlayerName(playerName) 
            : cacheManager.getTeamByPlayerName(playerName);
    }

    public static Team initFromPlayerUuid(UUID playerUuid){
        return cacheManager.getTeamByPlayerUuid(playerUuid) == null 
            ? plugin.getDatabaseManager().getTeamFromPlayerName(plugin.getSQLiteManager().getPlayerName(playerUuid)) 
            : cacheManager.getTeamByPlayerUuid(playerUuid);
    }

    public static Team init(UUID teamUuid){
        return cacheManager.getTeamByUuid(teamUuid) == null 
            ? plugin.getDatabaseManager().getTeamFromTeamUuid(teamUuid) 
            : cacheManager.getTeamByUuid(teamUuid);
    }

    public static Team getFromCache(Player player){
        return cacheManager.getTeamByPlayerName(player.getName());
    }


    public boolean isTeamMember(String playerName){
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(playerName)) return true;
        }
        return false;
    }


    public boolean isTeamMember(UUID playerUuid){
        for (Member member : this.teamMembers) {
            if (member.getUuid().equals(playerUuid)) return true;
        }
        return false;
    }


    public boolean isTeamAlliance(UUID allianceUuid){
        for (Alliance alliance : this.teamAlliances) {
            if (alliance.getTeamUuid().equals(allianceUuid)) return true;
        }
        return false;
    }


    public boolean isTeamOwner(String playerName){
        if (this.teamOwnerName.equals(playerName)) return true;
        return false;
    }


    public void updateTeamName(String newTeamName){
        plugin.getDatabaseManager().updateTeamName(this.teamUuid, newTeamName);

        this.teamName = newTeamName;
    }


    public void updateTeamPrefix(String newPrefix){
        this.teamPrefix = newPrefix;
        plugin.getDatabaseManager().updateTeamPrefix(teamUuid, newPrefix);
    }


    public void updateTeamDescription(String newDescription){
        this.teamDescription = newDescription;
        plugin.getDatabaseManager().updateTeamDescription(teamUuid, newDescription);
    }


    public void updateTeamOwner(String newOwnerName){
        plugin.getDatabaseManager().updateTeamOwner(teamUuid, teamOwnerName, newOwnerName);
        this.teamOwnerName = newOwnerName;
    }


    public void unlockedTeamBank(){
        this.unlockedTeamBank = true;
        plugin.getDatabaseManager().updateUnlockedTeamBank(teamUuid);
    }


    public void insertMember(String playerName){
        this.teamMembers.add(
            new Member(
                playerName,
                plugin.getLastRank(),
                getCurrentDate(),
                StelyTeamPlugin.getPlugin().getSQLiteManager().getUUID(playerName)
            )
        );
        plugin.getDatabaseManager().insertMember(playerName, teamUuid);
    }


    public void insertAlliance(UUID allianceUuid){
        this.teamAlliances.add(new Alliance(allianceUuid, getCurrentDate()));
        plugin.getDatabaseManager().insertAlliance(teamUuid, allianceUuid);
    }


    public void removeMember(String playerName){
        this.teamMembers.removeIf(member -> member.getMemberName().equals(playerName));
        plugin.getDatabaseManager().removeMember(playerName, teamUuid);
    }


    public void removeAlliance(UUID allianceUuid){
        this.teamAlliances.removeIf(alliance -> alliance.getTeamUuid().equals(allianceUuid));
        plugin.getDatabaseManager().removeAlliance(teamUuid, allianceUuid);
    }


    public void removeTeam(){
        plugin.getDatabaseManager().removeTeam(teamUuid);
        plugin.getSQLiteManager().removeHome(teamUuid);
        cacheManager.removeTeam(this);
    }


    public void incrementTeamStorageLvl(){
        this.teamStorageLvl++;
        plugin.getDatabaseManager().incrementTeamStorageLvl(teamUuid);
    }


    public void incrementImprovLvlMembers(){
        this.improvLvlMembers++;
        plugin.getDatabaseManager().incrementImprovLvlMembers(teamUuid);
    }


    public void incrementAssignement(String permissionName){
        for (Permission permission : this.teamPermissions) {
            if (permission.getPermissionName().equals(permissionName)){
                permission.incrementTeamRank();
                plugin.getDatabaseManager().incrementAssignement(teamUuid, permissionName);
                return;
            }
        }
    }


    public void decrementImprovLvlMembers(){
        this.improvLvlMembers--;
        plugin.getDatabaseManager().decrementImprovLvlMembers(teamUuid);
    }


    public void decrementAssignement(String permissionName){
        for (Permission permission : this.teamPermissions) {
            if (permission.getPermissionName().equals(permissionName)){
                permission.decrementTeamRank();
                plugin.getDatabaseManager().decrementAssignement(teamUuid, permissionName);
                return;
            }
        }
    }


    public void insertAssignement(String permissionName, Integer teamRank){
        this.teamPermissions.add(new Permission(permissionName, teamRank));
        plugin.getDatabaseManager().insertAssignement(teamUuid, permissionName, teamRank);
    }


    public void incrementTeamMoney(Double amount){
        this.teamMoney += amount;
        plugin.getDatabaseManager().incrementTeamMoney(teamUuid, amount);
    }


    public void decrementTeamMoney(Double amount){
        this.teamMoney -= amount;
        plugin.getDatabaseManager().decrementTeamMoney(teamUuid, amount);
    }


    public void refreshTeamMembersInventory(String authorName) {
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(authorName)) continue;

            Player player = Bukkit.getPlayer(member.getMemberName());
            if (player != null) {
                if (!config.getConfigurationSection("inventoriesName").getValues(true).containsValue(player.getOpenInventory().getTitle())){
                    continue;
                }
                
                String openInventoryTitle = player.getOpenInventory().getTitle();
                if (openInventoryTitle.equals(config.getString("inventoriesName.admin"))){
                    new AdminMenu(player).openMenu();
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.manage"))){
                    new ManageMenu(player).openMenu(this);
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.member"))){
                    new MemberMenu(player).openMenu(this);
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.upgradeTotalMembers"))){
                    new UpgradeMembersMenu(player).openMenu(this);
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.editMembers"))){
                    new EditMembersMenu(player).openMenu(this);
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.teamMembers"))){
                    new MembersMenu(player).openMenu(this);
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.permissions"))){
                    new PermissionsMenu(player).openMenu(this);
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.storageDirectory"))){
                    new StorageDirectoryMenu(player).openMenu(this);
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.editAlliances"))){
                    new EditAlliancesMenu(player).openMenu(this);
                }
            }
        }
    }


    public void closeTeamMembersInventory(String authorName) {
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(member.getMemberName());
            if (player != null) {
                if (!config.getConfigurationSection("inventoriesName").getValues(true).containsValue(player.getOpenInventory().getTitle())){
                    continue;
                }

                player.closeInventory();
            }
        }
    }


    public void teamBroadcast(String authorName, String message) {
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(member.getMemberName());
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }


    public ArrayList<String> getMembersName(){
        ArrayList<String> membersName = new ArrayList<String>();
        for (Member member : this.teamMembers) {
            membersName.add(member.getMemberName());
        }
        return membersName;
    }


    public Member getMember(String playerName){
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(playerName)) return member;
        }
        return null;
    }


    public String getMembershipDate(String playerName){
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(playerName)) return member.getJoinDate();
        }
        return null;
    }


    public Integer getPermissionRank(String permissionName){
        for (Permission permission : this.teamPermissions) {
            if (permission.getPermissionName().equals(permissionName)) return permission.getTeamRank();
        }
        return null;
    }


    public Integer getMemberRank(String playerName){
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(playerName)) return member.getTeamRank();
        }
        return null;
    }


    public boolean hasStorage(int storageId){
        return this.teamStorages.containsKey(storageId);
    }


    public Storage getStorage(int storageId){
        return this.teamStorages.get(storageId);
    }


    public UUID getTeamUuid() {
        return teamUuid;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamPrefix() {
        return teamPrefix;
    }

    public String getTeamDescription() {
        return teamDescription;
    }

    public Double getTeamMoney() {
        return teamMoney;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public Integer getImprovLvlMembers() {
        return improvLvlMembers;
    }

    public Integer getTeamStorageLvl() {
        return teamStorageLvl;
    }

    public boolean isUnlockedTeamBank() {
        return unlockedTeamBank;
    }

    public String getTeamOwnerName() {
        return teamOwnerName;
    }

    public Set<Member> getTeamMembers() {
        return teamMembers;
    }

    public Set<Permission> getTeamPermissions() {
        return teamPermissions;
    }

    public Set<Alliance> getTeamAlliances() {
        return teamAlliances;
    }


    public void setTeamPrefix(String teamPrefix) {
        this.teamPrefix = teamPrefix;
    }

    private String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }
}