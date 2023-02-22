package fr.army.stelyteam.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.builder.InventoryBuilder;

public class Team {
    private StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private YamlConfiguration config = plugin.getConfig();

    private String teamName;
    private String teamPrefix = null;
    private String teamDescription = config.getString("team.defaultDescription");
    private Double teamMoney = 0.0;
    private String creationDate = getCurrentDate();
    private Integer improvLvlMembers = 0;
    private Integer teamStorageLvl = 0;
    private boolean unlockedTeamBank = false;
    private String teamOwnerName;
    private ArrayList<Member> teamMembers;
    private ArrayList<Permission> teamPermissions;
    private ArrayList<Alliance> teamAlliances;

    public Team(String teamName, String teamPrefix, String teamDescription, double teamMoney, String creationDate, int improvLvlMembers, int teamStorageLvl, boolean unlockedTeamBank, String teamOwnerName){
        this.teamName = teamName;
        this.teamPrefix = teamPrefix;
        this.teamDescription = teamDescription;
        this.teamMoney = teamMoney;
        this.creationDate = creationDate;
        this.improvLvlMembers = improvLvlMembers;
        this.teamStorageLvl = teamStorageLvl;
        this.unlockedTeamBank = unlockedTeamBank;
        this.teamOwnerName = teamOwnerName;

        this.teamMembers = this.plugin.getDatabaseManager().getTeamMembers(teamName);
        this.teamPermissions = this.plugin.getDatabaseManager().getTeamAssignement(teamName);
        this.teamAlliances = this.plugin.getDatabaseManager().getTeamAlliances(teamName);
    }

    public Team(String teamName, String teamPrefix, String creationDate, String teamOwnerName){
        this.teamName = teamName;
        this.teamPrefix = teamPrefix;
        this.teamOwnerName = teamOwnerName;
    }

    public Team(String teamName, String teamOwnerName){
        this.teamName = teamName;
        this.teamOwnerName = teamOwnerName;
    }


    public void createTeam(){
        this.plugin.getDatabaseManager().insertTeam(teamName, teamPrefix, teamOwnerName);
    }


    public boolean isTeamMember(String playerName){
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(playerName)) return true;
        }
        return false;
    }


    public boolean isTeamAlliance(String allianceName){
        for (Alliance alliance : this.teamAlliances) {
            if (alliance.getTeamName().equals(allianceName)) return true;
        }
        return false;
    }


    public boolean isTeamOwner(String playerName){
        if (this.teamOwnerName.equals(playerName)) return true;
        return false;
    }


    public void updateTeamName(String newTeamName){
        this.teamName = newTeamName;
        this.plugin.getCacheManager().replaceStorageTeam(this.teamName, this);
        this.plugin.getDatabaseManager().updateTeamName(teamName, newTeamName);
        this.plugin.getSQLiteManager().updateTeamID(teamName, newTeamName);
    }


    public void updateTeamPrefix(String newPrefix){
        this.teamPrefix = newPrefix;
        this.plugin.getDatabaseManager().updateTeamPrefix(teamName, newPrefix);
    }


    public void updateTeamDescription(String newDescription){
        this.teamDescription = newDescription;
        this.plugin.getDatabaseManager().updateTeamDescription(teamName, newDescription);
    }


    public void updateTeamOwner(String newOwnerName){
        this.teamOwnerName = newOwnerName;
        this.plugin.getDatabaseManager().updateTeamOwner(teamName, teamOwnerName, newOwnerName);
    }


    public void unlockedTeamBank(){
        this.unlockedTeamBank = true;
        this.plugin.getDatabaseManager().updateUnlockedTeamBank(teamName);
    }


    public void insertMember(String playerName){
        this.teamMembers.add(new Member(playerName, this.plugin.getLastRank(), getCurrentDate()));
        this.plugin.getDatabaseManager().insertMember(playerName, teamName);
    }


    public void insertAlliance(String allianceName){
        this.teamAlliances.add(new Alliance(allianceName, getCurrentDate()));
        this.plugin.getDatabaseManager().insertAlliance(teamName, allianceName);
    }


    public void removeMember(String playerName){
        this.teamMembers.removeIf(member -> member.getMemberName().equals(playerName));
        this.plugin.getDatabaseManager().removeMember(playerName, teamName);
    }


    public void removeAlliance(String allianceName){
        this.teamAlliances.removeIf(alliance -> alliance.getTeamName().equals(allianceName));
        this.plugin.getDatabaseManager().removeAlliance(teamName, allianceName);
    }


    public void removeTeam(){
        this.plugin.getDatabaseManager().removeTeam(teamName);
        this.plugin.getSQLiteManager().removeHome(teamName);
    }


    public void incrementTeamStorageLvl(){
        this.teamStorageLvl++;
        this.plugin.getDatabaseManager().incrementTeamStorageLvl(teamName);
    }


    public void incrementImprovLvlMembers(){
        this.improvLvlMembers++;
        this.plugin.getDatabaseManager().incrementImprovLvlMembers(teamName);
    }


    public void incrementAssignement(String permissionName){
        for (Permission permission : this.teamPermissions) {
            if (permission.getPermissionName().equals(permissionName)){
                permission.incrementTeamRank();
                this.plugin.getDatabaseManager().incrementAssignement(teamName, permissionName);
                return;
            }
        }
    }


    public void decrementImprovLvlMembers(){
        this.improvLvlMembers--;
        this.plugin.getDatabaseManager().decrementImprovLvlMembers(teamName);
    }


    public void decrementAssignement(String permissionName){
        for (Permission permission : this.teamPermissions) {
            if (permission.getPermissionName().equals(permissionName)){
                permission.decrementTeamRank();
                this.plugin.getDatabaseManager().decrementAssignement(teamName, permissionName);
                return;
            }
        }
    }


    public void insertAssignement(String permissionName, Integer teamRank){
        this.teamPermissions.add(new Permission(permissionName, teamRank));
        this.plugin.getDatabaseManager().insertAssignement(teamName, permissionName, teamRank);
    }


    public void incrementTeamMoney(Double amount){
        this.teamMoney += amount;
        this.plugin.getDatabaseManager().incrementTeamMoney(teamName, amount);
    }


    public void decrementTeamMoney(Double amount){
        this.teamMoney -= amount;
        this.plugin.getDatabaseManager().decrementTeamMoney(teamName, amount);
    }


    public void refreshTeamMembersInventory(String authorName) {
        InventoryBuilder inventoryBuilder = this.plugin.getInventoryBuilder();
        for (Member member : this.teamMembers) {
            if (member.getMemberName().equals(authorName)) continue;

            HumanEntity player = Bukkit.getPlayer(member.getMemberName());
            if (player != null) {
                if (!config.getConfigurationSection("inventoriesName").getValues(true).containsValue(player.getOpenInventory().getTitle())){
                    continue;
                }
                
                String openInventoryTitle = player.getOpenInventory().getTitle();
                if (openInventoryTitle.equals(config.getString("inventoriesName.admin"))){
                    player.openInventory(inventoryBuilder.createAdminInventory());
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.manage"))){
                    player.openInventory(inventoryBuilder.createManageInventory(player.getName(), this));
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.member"))){
                    player.openInventory(inventoryBuilder.createMemberInventory(player.getName(), this));
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.upgradeTotalMembers"))){
                    player.openInventory(inventoryBuilder.createUpgradeTotalMembersInventory(player.getName(), this));
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.editMembers"))){
                    player.openInventory(inventoryBuilder.createEditMembersInventory(player.getName(), this));
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.teamMembers"))){
                    player.openInventory(inventoryBuilder.createMembersInventory(this));
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.permissions"))){
                    player.openInventory(inventoryBuilder.createPermissionsInventory(player.getName(), this));
                }else if (openInventoryTitle.equals(config.getString("inventoriesName.storageDirectory"))){
                    player.openInventory(inventoryBuilder.createStorageDirectoryInventory(player.getName(), this));
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


    public void setTeamPrefix(String teamPrefix) {
        this.teamPrefix = teamPrefix;
    }

    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }

    public void setTeamMoney(Integer teamMoney) {
        this.teamMoney = teamMoney;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setTeamMembersCount(int teamMembersCount) {
        this.teamMembersCount = teamMembersCount;
    }

    public void setImprovLvlMembers(Integer improvLvlMembers) {
        this.improvLvlMembers = improvLvlMembers;
    }

    public void setTeamStorageLvl(Integer teamStorageLvl) {
        this.teamStorageLvl = teamStorageLvl;
    }

    public void setUnlockedTeamBank(boolean unlockedTeamBank) {
        this.unlockedTeamBank = unlockedTeamBank;
    }

    public void setTeamOwnerName(String teamOwnerName) {
        this.teamOwnerName = teamOwnerName;
    }

    public void setTeamMembers(ArrayList<Member> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public void setTeamPermissions(ArrayList<Permission> teamPermissions) {
        this.teamPermissions = teamPermissions;
    }

    public void setTeamAlliances(ArrayList<Alliance> teamAlliances) {
        this.teamAlliances = teamAlliances;
    }

    private String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }
}
