package fr.army.stelyteam.team;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.utils.manager.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.text.SimpleDateFormat;
import java.util.*;

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
    private boolean unlockedTeamBank = false;
    private boolean unlockedTeamClaim = false;
    private Member teamOwner;
    private Collection<Member> teamMembers = new ArrayList<>();
    private Set<Permission> teamPermissions = new HashSet<>();
    private List<Alliance> teamAlliances = new ArrayList<>();
    private Map<Integer, Storage> teamStorages;

    // Existing team
    public Team(UUID teamUuid, String teamName, String teamPrefix, String teamDescription, double teamMoney,
            String creationDate, int improvLvlMembers, int teamStorageLvl, boolean unlockedTeamBank,
            boolean unlockedTeamClaim, String teamOwnerName) {
        this.teamUuid = teamUuid;
        this.teamName = teamName;
        this.teamPrefix = teamPrefix;
        this.teamDescription = teamDescription;
        this.teamMoney = teamMoney;
        this.creationDate = creationDate;
        this.improvLvlMembers = improvLvlMembers;
        this.teamStorageLvl = teamStorageLvl;
        this.unlockedTeamBank = unlockedTeamBank;
        this.unlockedTeamClaim = unlockedTeamClaim;
        this.teamOwner = plugin.getDatabaseManager().getTeamMember(teamUuid, teamOwnerName);

        this.teamMembers = plugin.getDatabaseManager().getTeamMembers(teamUuid);
        this.teamPermissions = plugin.getDatabaseManager().getTeamAssignement(teamUuid);
        this.teamAlliances = plugin.getDatabaseManager().getTeamAlliances(teamUuid);
        this.teamStorages = plugin.getDatabaseManager().getTeamStorages(teamUuid);
    }

    // New team
    public Team(String teamName, String teamOwnerName){
        this.teamUuid = UUID.randomUUID();
        this.teamName = teamName;
        this.teamOwner = null; // TODO: revoir
    }


    public void createTeam(){
        plugin.getDatabaseManager().insertTeam(this);
        cacheManager.addTeam(Team.init(teamUuid));
    }

    public static Team init(String teamName){
        // return cacheManager.getTeamByName(teamName) == null 
        //     ? plugin.getDatabaseManager().getTeamFromTeamName(teamName) 
        //     : cacheManager.getTeamByName(teamName);
        return plugin.getDatabaseManager().getTeamFromTeamName(teamName);
    }

    public static Team init(Player player){
        // return cacheManager.getTeamByPlayerName(player.getName()) == null 
        //     ? plugin.getDatabaseManager().getTeamFromPlayerName(player.getName()) 
        //     : cacheManager.getTeamByPlayerName(player.getName());
        return plugin.getDatabaseManager().getTeamFromPlayerName(player.getName());
    }

    public static Team initFromPlayerName(String playerName){
        // return cacheManager.getTeamByPlayerName(playerName) == null 
        //     ? plugin.getDatabaseManager().getTeamFromPlayerName(playerName) 
        //     : cacheManager.getTeamByPlayerName(playerName);
        return plugin.getDatabaseManager().getTeamFromPlayerName(playerName);
    }

    public static Team initFromPlayerUuid(UUID playerUuid){
        // return cacheManager.getTeamByPlayerUuid(playerUuid) == null 
        //     ? plugin.getDatabaseManager().getTeamFromPlayerName(plugin.getSQLiteManager().getPlayerName(playerUuid)) 
        //     : cacheManager.getTeamByPlayerUuid(playerUuid);
        return plugin.getDatabaseManager().getTeamFromPlayerName(plugin.getDatabaseManager().getPlayerName(playerUuid));
    }

    public static Team init(UUID teamUuid){
        // return cacheManager.getTeamByUuid(teamUuid) == null 
        //     ? plugin.getDatabaseManager().getTeamFromTeamUuid(teamUuid) 
        //     : cacheManager.getTeamByUuid(teamUuid);
        return plugin.getDatabaseManager().getTeamFromTeamUuid(teamUuid);
    }

    public static Team getFromCache(Player player){
        return cacheManager.getTeamByPlayerUuid(player.getUniqueId());
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
        // cacheManager.replaceTeam(this);

        this.teamName = newTeamName;
    }


    public void updateTeamPrefix(String newPrefix){
        plugin.getDatabaseManager().updateTeamPrefix(teamUuid, newPrefix);
        cacheManager.replaceTeam(this);
        
        this.teamPrefix = newPrefix;
    }


    public void updateTeamDescription(String newDescription){
        plugin.getDatabaseManager().updateTeamDescription(teamUuid, newDescription);
        // cacheManager.replaceTeam(this);

        this.teamDescription = newDescription;
    }


    public void updateTeamOwner(String newOwnerName){
        plugin.getDatabaseManager().updateTeamOwner(teamUuid, teamOwnerName, newOwnerName);
        cacheManager.replaceTeam(this);

        this.teamOwnerName = newOwnerName;
    }


    public void unlockedTeamBank(){
        plugin.getDatabaseManager().updateUnlockedTeamBank(teamUuid);
        this.unlockedTeamBank = true;
    }

    public void unlockedTeamClaim(){
        plugin.getDatabaseManager().updateUnlockedTeamClaim(teamUuid);
        this.unlockedTeamClaim = true;
    }


    public void insertMember(String playerName){
        this.teamMembers.add(
            new Member(
                playerName,
                plugin.getLastRank(),
                getCurrentDate(),
                plugin.getDatabaseManager().getUUID(playerName)
            )
        );
        plugin.getDatabaseManager().insertMember(playerName, teamUuid);
    }


    public void insertAlliance(Team teamAlliance){
        this.teamAlliances.add(new Alliance(teamAlliance.getTeamUuid(), getCurrentDate()));
        teamAlliance.getTeamAlliances().add(new Alliance(getTeamUuid(), getCurrentDate()));
        plugin.getDatabaseManager().insertAlliance(teamUuid, teamAlliance.getTeamUuid());
    }


    public void removeMember(String playerName){
        this.teamMembers.removeIf(member -> member.getMemberName().equals(playerName));
        plugin.getDatabaseManager().removeMember(playerName, teamUuid);
    }


    public void removeAlliance(Team teamAlliance){
        this.teamAlliances.removeIf(alliance -> alliance.getTeamUuid().equals(teamAlliance.getTeamUuid()));
        teamAlliance.getTeamAlliances().removeIf(alliance -> alliance.getTeamUuid().equals(teamUuid));
        plugin.getDatabaseManager().removeAlliance(teamUuid, teamAlliance.getTeamUuid());
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
            // if (member.getMemberName().equals(authorName)) continue;

            Player player = Bukkit.getPlayer(member.getUuid());
            if (player != null) {
                if (!config.getConfigurationSection("inventoriesName").getValues(true).containsValue(player.getOpenInventory().getTitle())){
                    continue;
                }
                
                InventoryView inventoryView = player.getOpenInventory();
                if (inventoryView.getTopInventory().getHolder() instanceof TeamMenuOLD){
                    ((TeamMenuOLD) inventoryView.getTopInventory().getHolder()).openMenu();
                    // System.out.println("refreshed");
                }
                // if (openInventoryTitle.equals(config.getString("inventoriesName.admin"))){
                //     new AdminMenu(player).openMenu(null);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.manage"))){
                //     new ManageMenu(player).openMenu(this);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.member"))){
                //     new MemberMenu(player).openMenu(this);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.upgradeTotalMembers"))){
                //     new UpgradeMembersMenu(player).openMenu(this);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.editMembers"))){
                //     new EditMembersMenu(player).openMenu(this);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.teamMembers"))){
                //     new MembersMenu(player).openMenu(this);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.permissions"))){
                //     new PermissionsMenu(player).openMenu(this);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.storageDirectory"))){
                //     new StorageDirectoryMenu(player).openMenu(this);
                // }else if (openInventoryTitle.equals(config.getString("inventoriesName.editAlliances"))){
                //     new EditAlliancesMenu(player).openMenu(this);
                // }
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


    public List<String> getMembersName(){
        ArrayList<String> membersName = new ArrayList<String>();
        for (Member member : this.teamMembers) {
            membersName.add(member.getMemberName());
        }
        return membersName;
    }


    public Set<UUID> getMembersUuid(){
        Set<UUID> membersUuid = new HashSet<UUID>();
        for (Member member : this.teamMembers) {
            membersUuid.add(member.getUuid());
        }
        return membersUuid;
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

    public boolean isUnlockedTeamClaim(){
        return unlockedTeamClaim;
    }

    public Member getTeamOwner() {
        return teamOwnerName;
    }

    public Collection<Member> getTeamMembers() {
        return teamMembers;
    }

    public Set<Permission> getTeamPermissions() {
        return teamPermissions;
    }

    public List<Alliance> getTeamAlliances() {
        return teamAlliances;
    }


    public void setTeamPrefix(String teamPrefix) {
        this.teamPrefix = teamPrefix;
    }

    public void refreshTeamStorage(){
        this.teamStorages = plugin.getDatabaseManager().getTeamStorages(teamUuid);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Team team)) {
            return false;
        }
        return this.teamUuid.equals(team.teamUuid);
    }

    @Override
    public int hashCode() {
        return teamUuid.hashCode();
    }

}