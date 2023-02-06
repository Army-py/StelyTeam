package fr.army.stelyteam.utils.builder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Alliance;
import fr.army.stelyteam.utils.Member;
import fr.army.stelyteam.utils.Page;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MySQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;
import fr.army.stelyteam.utils.manager.serializer.ItemStackSerializer;

public class InventoryBuilder {

    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private MySQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private ItemStackSerializer serializeManager;
    private ColorsBuilder colorsBuilder;


    public InventoryBuilder(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.serializeManager = new ItemStackSerializer();
        this.colorsBuilder = new ColorsBuilder(plugin);
    }


	public Inventory createTeamInventory() {
		Integer slots = config.getInt("inventoriesSlots.createTeam");
		Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.createTeam"));
		
        emptyCases(inventory, slots, 0);
		
        Integer slot = config.getInt("inventories.createTeam.slot");
        Material material = Material.getMaterial(config.getString("inventories.createTeam.itemType"));
        String name = config.getString("inventories.createTeam.itemName");
        List<String> lore = config.getStringList("inventories.createTeam.lore");
        String headTexture = config.getString("inventories.createTeam.headTexture");

        inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
		
		return inventory;
	}


    public Inventory createAdminInventory() {
        Integer slots = config.getInt("inventoriesSlots.admin");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.admin"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.admin").getKeys(false)){
            Integer slot = config.getInt("inventories.admin."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.admin."+str+".itemType"));
            String name = config.getString("inventories.admin."+str+".itemName");
            List<String> lore = config.getStringList("inventories.admin."+str+".lore");
            String headTexture = config.getString("inventories.admin."+str+".headTexture");
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }
        return inventory;
    }


    public Inventory createManageInventory(String playername, Team team) {
        Integer slots = config.getInt("inventoriesSlots.manage");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.manage"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.manage").getKeys(false)){
            Integer slot = config.getInt("inventories.manage."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.manage."+str+".itemType"));
            String name = config.getString("inventories.manage."+str+".itemName");
            List<String> lore = config.getStringList("inventories.manage."+str+".lore");
            String headTexture = config.getString("inventories.manage."+str+".headTexture");
            
            ItemStack item;
            
            if (plugin.playerHasPermission(playername, team, str)){ 
                if (str.equals("buyTeamBank")){
                    item = ItemBuilder.getItem(material, name, lore, headTexture, team.isUnlockedTeamBank());
                }else {
                    item = ItemBuilder.getItem(material, name, lore, headTexture, false);
                }
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(slot, item);
        }
        return inventory;
    }


    public Inventory createMemberInventory(String playername, Team team) {
        Integer slots = config.getInt("inventoriesSlots.member");
        String teamName = team.getTeamName();
        String teamPrefix = team.getTeamPrefix();
        String teamOwner = team.getTeamOwnerName();
        Integer teamMembersLelvel = team.getImprovLvlMembers();
        Integer teamMembers = team.getTeamMembers().size();
        String membershipDate = team.getMembershipDate(playername);
        Double teamMoney = team.getTeamMoney();
        String teamDescription = team.getTeamDescription();
        Integer maxMembers = config.getInt("teamMaxMembers");
        String memberRank = plugin.getRankFromId(team.getMemberRank(playername));
        String memberRankName = config.getString("ranks." + memberRank + ".name");
        String rankColor = config.getString("ranks." + memberRank + ".color");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.member"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.member").getKeys(false)){
            Integer slot = config.getInt("inventories.member."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.member."+str+".itemType"));
            String name = config.getString("inventories.member."+str+".itemName");
            List<String> lore = config.getStringList("inventories.member."+str+".lore");
            String headTexture = config.getString("inventories.member."+str+".headTexture");
            ItemStack item;

            if (name.equals(config.getString("inventories.member.seeTeamBank.itemName"))){
                lore = replaceInLore(lore, "%TEAM_MONEY%", DoubleToString(teamMoney));
                lore = replaceInLore(lore, "%MAX_MONEY%", DoubleToString(config.getDouble("teamMaxMoney")));
            }else if (name.equals(config.getString("inventories.member.teamInfos.itemName"))){
                lore = replaceInLore(lore, "%NAME%", teamName);
                lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(teamPrefix));
                lore = replaceInLore(lore, "%OWNER%", teamOwner);
                lore = replaceInLore(lore, "%RANK%", rankColor + memberRankName);
                lore = replaceInLore(lore, "%DATE%", membershipDate);
                lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
                lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
                lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(teamDescription));
            }

            if (plugin.playerHasPermission(playername, team, str)){ 
                item = ItemBuilder.getItem(material, name, lore, headTexture, false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            if (name.equals(config.getString("inventories.member.seeTeamBank.itemName"))){
                if (!team.isUnlockedTeamBank()){
                    item = ItemBuilder.getItem(
                        Material.getMaterial(config.getString("teamBankNotUnlock.itemType")),
                        config.getString("teamBankNotUnlock.itemName"),
                        Collections.emptyList(),
                        config.getString("teamBankNotUnlock.headTexture"),
                        false
                    );
                }
            }

            inventory.setItem(slot,  item);
        }
        return inventory;
    }


    public Inventory createConfirmInventory() {
        Integer slots = config.getInt("inventoriesSlots.confirmInventory");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.confirmInventory"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.confirmInventory").getKeys(false)){
            Material material = Material.getMaterial(config.getString("inventories.confirmInventory."+str+".itemType"));
            String name = config.getString("inventories.confirmInventory."+str+".itemName");
            List<String> lore = config.getStringList("inventories.confirmInventory."+str+".lore");
            String headTexture = config.getString("inventories.confirmInventory."+str+".headTexture");

            for(Integer slot : config.getIntegerList("inventories.confirmInventory."+str+".slots")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            }
        }
        return inventory;
    }


    public Inventory createUpgradeTotalMembersInventory(String playername, Team team) {
        Integer slots = config.getInt("inventoriesSlots.upgradeTotalMembers");
        Integer level = team.getImprovLvlMembers();
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.upgradeTotalMembers"));
        Material material;
        String headTexture;

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeTotalMembers."+str+".slot");
            // Material material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+str+".itemType"));
            String name = config.getString("inventories.upgradeTotalMembers."+str+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeTotalMembers."+str+".lore");
            // String headTexture = config.getString("inventories.upgradeTotalMembers."+str+".headTexture");

            // if (level >= config.getInt("inventories.upgradeTotalMembers."+str+".level") && !str.equals("close")){
            //     inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, true));
            // }else{
            //     inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            // }

            if (str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+str+".itemType"));
                headTexture = config.getString("inventories.upgradeTotalMembers."+str+".headTexture");
            }else if (level >= config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+str+".unlock.itemType"));
                headTexture = config.getString("inventories.upgradeTotalMembers."+str+".unlock.headTexture");
            }else{
                material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+str+".lock.itemType"));
                headTexture = config.getString("inventories.upgradeTotalMembers."+str+".lock.headTexture");
            }
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }
        return inventory;
    }


    public Inventory createUpgradeStorageInventory(String playername, Team team) {
        Integer slots = config.getInt("inventoriesSlots.upgradeStorageAmount");
        Integer level = team.getTeamStorageLvl();
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.upgradeStorageAmount"));
        Material material;
        String headTexture;

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.upgradeStorageAmount").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeStorageAmount."+str+".slot");
            // Material material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".itemType"));
            String name = config.getString("inventories.upgradeStorageAmount."+str+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeStorageAmount."+str+".lore");
            // String headTexture = config.getString("inventories.upgradeStorageAmount."+str+".headTexture");

            // if (level >= config.getInt("inventories.upgradeStorageAmount."+str+".level") && !str.equals("close")){
            //     inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, true));
            // }else{
            //     inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            // }
            
            if (str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+str+".headTexture");
            }else if (level >= config.getInt("inventories.upgradeStorageAmount."+str+".level")){
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".unlock.itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+str+".unlock.headTexture");
            }else{
                material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".lock.itemType"));
                headTexture = config.getString("inventories.upgradeStorageAmount."+str+".lock.headTexture");
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }
        return inventory;
    }


    public Inventory createMembersInventory(Team team) {
        Integer slots = config.getInt("inventoriesSlots.teamMembers");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.teamMembers"));

        emptyCases(inventory, slots, 0);
        Integer headSlot = 0;
        for(Member member : team.getTeamMembers()){
            String memberName = member.getMemberName();
            UUID playerUUID = sqliteManager.getUUID(member.getMemberName());
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer offlinePlayer;

            if (playerUUID == null) offlinePlayer = Bukkit.getOfflinePlayer(memberName);
            else offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);

            String memberRank = plugin.getRankFromId(team.getMemberRank(memberName));
            String rankColor = config.getString("ranks." + memberRank + ".color");
            itemName = rankColor + memberName;
            
            lore.add(config.getString("prefixRankLore") + rankColor + config.getString("ranks." + memberRank + ".name"));
            inventory.setItem(headSlot, ItemBuilder.getPlayerHead(offlinePlayer, itemName, lore));
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.teamMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.teamMembers."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamMembers."+str+".itemType"));
            String name = config.getString("inventories.teamMembers."+str+".itemName");
            String headTexture = config.getString("inventories.teamMembers."+str+".headTexture");

            // if (sqlManager.isOwner(playername)) lore = config.getStringList("inventories.teamMembers."+str+".lore");
            // else lore = Collections.emptyList();
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, Collections.emptyList(), headTexture, false));
        }
        return inventory;
    }


    public Inventory createEditMembersInventory(String playerName, Team team) {
        Integer slots = config.getInt("inventoriesSlots.editMembers");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.editMembers"));

        emptyCases(inventory, slots, 0);
        Integer headSlot = 0;
        for(String memberName : team.getMembersName()){
            UUID playerUUID = sqliteManager.getUUID(memberName);
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer member;
            ItemStack item;

            if (playerUUID == null) member = Bukkit.getOfflinePlayer(memberName);
            else member = Bukkit.getOfflinePlayer(playerUUID);

            Integer memberRank = team.getMemberRank(memberName);
            String memberRankName = plugin.getRankFromId(memberRank);
            String rankColor = config.getString("ranks." + memberRankName + ".color");
            itemName = rankColor + memberName;
            
            if (!team.isTeamOwner(memberName)){
                lore = config.getStringList("editMembersLores");
                if (plugin.getLastRank() == memberRank) lore.remove(1);
            }

            lore.add(0, config.getString("prefixRankLore") + rankColor + config.getString("ranks." + memberRankName + ".name"));
            
            if (plugin.playerHasPermission(playerName, team, "manageMembers")){ 
                item = ItemBuilder.getPlayerHead(member, itemName, lore);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    itemName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.editMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.editMembers."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.editMembers."+str+".itemType"));
            String name = config.getString("inventories.editMembers."+str+".itemName");
            String headTexture = config.getString("inventories.editMembers."+str+".headTexture");
            ItemStack item;

            // if (sqlManager.isOwner(playerName)) lore = config.getStringList("inventories.editMembers."+str+".lore");
            // else lore = Collections.emptyList();
            

            // if (plugin.playerHasPermission(playerName, teamName, str)){ 
            //     item = ItemBuilder.getItem(material, name, lore, headTexture, false);
            // }else{
            //     item = ItemBuilder.getItem(
            //         Material.getMaterial(config.getString("noPermission.itemType")), 
            //         name, 
            //         config.getStringList("noPermission.lore"),
            //         config.getString("noPermission.headTexture"),
            //         false
            //     );
            // }

            if (plugin.playerHasPermission(playerName, team, str)){ 
                item = ItemBuilder.getItem(
                    material,
                    name,
                    config.getStringList("inventories.editMembers."+str+".lore"),
                    headTexture,
                    false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(slot, item);
        }
        return inventory;
    }


    public Inventory createPermissionsInventory(String playerName, Team team) {
        Integer slots = config.getInt("inventoriesSlots.permissions");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.permissions"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.permissions").getKeys(false)){
            Integer slot = config.getInt("inventories.permissions."+str+".slot");
            // Material material = Material.getMaterial(config.getString("inventories.permissions."+str+".itemType"));
            String name = config.getString("inventories.permissions."+str+".itemName");
            List<String> lore = config.getStringList("inventories.permissions."+str+".lore");
            String headTexture;
            
            String rankPath = config.getString("inventories.permissions."+str+".rankPath");
            Integer defaultRankId = config.getInt("inventories."+rankPath+".rank");
            Integer permissionRank = team.getPermissionRank(str);
            String lorePrefix = config.getString("prefixRankLore");
            Material material;

            if (str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.permissions."+str+".itemType"));
                headTexture = config.getString("inventories.permissions."+str+".headTexture");
            }else{
                material = Material.getMaterial(config.getString("ranks."+plugin.getRankFromId(permissionRank != null ? permissionRank : defaultRankId)+".itemType"));
                headTexture = config.getString("ranks."+plugin.getRankFromId(permissionRank != null ? permissionRank : defaultRankId)+".headTexture");
            }

            if (permissionRank != null){
                String rankColor = config.getString("ranks." + plugin.getRankFromId(permissionRank) + ".color");
                lore.add(0, lorePrefix + rankColor + config.getString("ranks." + plugin.getRankFromId(permissionRank) + ".name"));
            }else if (rankPath != null){
                String rankColor = config.getString("ranks." + plugin.getRankFromId(defaultRankId) + ".color");
                lore.add(0, lorePrefix + rankColor + config.getString("ranks." + plugin.getRankFromId(defaultRankId) + ".name"));
            }

            boolean isDefault = false;
            if (!str.equals("close") && (permissionRank == null || defaultRankId == permissionRank)){
                isDefault = true;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, isDefault));
        }
        return inventory;
    }


    public Inventory createStorageDirectoryInventory(String playerName, Team team) {
        Integer slots = config.getInt("inventoriesSlots.storageDirectory");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.storageDirectory"));
        Integer level = team.getTeamStorageLvl();

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            Integer slot = config.getInt("inventories.storageDirectory."+str+".slot");
            String headTexture;
            Material material;
            String name;
            List<String> lore;

            if (level >= config.getInt("inventories.storageDirectory."+str+".level") || str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.storageDirectory."+str+".itemType"));
                headTexture = config.getString("inventories.storageDirectory."+str+".headTexture");
                if (str.equals("close")){
                    name = config.getString("inventories.storageDirectory."+str+".itemName");
                }else{
                    name = config.getString(config.getString("inventories.storageDirectory."+str+".itemName"));
                }
                lore = config.getStringList("inventories.storageDirectory."+str+".lore");

                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            }else{
                material = Material.getMaterial(config.getString("storageNotUnlock.itemType"));
                name = config.getString("storageNotUnlock.itemName");
                lore = config.getStringList("storageNotUnlock.lore");
                headTexture = config.getString("storageNotUnlock.headTexture");

                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            }
        }
        return inventory;
    }


    public Inventory createStorageInventory(Team team, Integer storageId, String storageName){
        Integer slots = config.getInt("inventoriesSlots.storage");
        Inventory inventory;

        if (cacheManager.containsStorage(team, storageId)){
            inventory = cacheManager.getStorage(team, storageId).getInventoryInstance();
        }else{
            inventory = Bukkit.createInventory(null, slots, storageName);

            if (sqlManager.teamHasStorage(team.getTeamName(), storageId)){
                byte[] contentString = sqlManager.getStorageContent(team.getTeamName(), storageId);
                ItemStack[] content = serializeManager.deserializeFromByte(contentString);
                inventory.setContents(content);
            }
        }

        emptyCases(inventory, config.getIntegerList("inventories.storage.emptyCase.slots"));

        for(String str : config.getConfigurationSection("inventories.storage").getKeys(false)){
            Integer slot = config.getInt("inventories.storage."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.storage."+str+".itemType"));
            String name = config.getString("inventories.storage."+str+".itemName");
            List<String> lore = config.getStringList("inventories.storage."+str+".lore");
            String headTexture = config.getString("inventories.storage."+str+".headTexture");

            if (str.equals("previous")){
                if (storageId == plugin.getMinStorageId()) continue;
            }else if (str.equals("next")){
                if (storageId == team.getTeamStorageLvl()) continue;
            }else if (str.equals("emptyCase")){
                continue;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }

        return inventory;
    }


    public Inventory createAlliancesInventory(String playername, Team team) {
        Integer slots = config.getInt("inventoriesSlots.teamAlliances");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.teamAlliances"));

        emptyCases(inventory, slots, 0);
        Integer headSlot = 0;
        for(Alliance alliance : team.getTeamAlliances()){
            Team teamAlliance = sqlManager.getTeamFromTeamName(alliance.getTeamName());
            String allianceName = teamAlliance.getTeamName();
            String alliancePrefix = teamAlliance.getTeamPrefix();
            String allianceOwnerName = teamAlliance.getTeamOwnerName();
            String allianceDate = alliance.getAllianceDate();
            Integer teamMembersLelvel = teamAlliance.getImprovLvlMembers();
            Integer teamMembers = teamAlliance.getTeamMembers().size();
            Integer maxMembers = config.getInt("teamMaxMembers");
            String allianceDescription = teamAlliance.getTeamDescription();
            ArrayList<String> allianceMembers = teamAlliance.getMembersName();
            UUID playerUUID = sqliteManager.getUUID(allianceOwnerName);
            String itemName = colorsBuilder.replaceColor(alliancePrefix);
            List<String> lore = config.getStringList("teamAllianceLore");
            OfflinePlayer allianceOwner;
            ItemStack item;

            if (playerUUID == null) allianceOwner = Bukkit.getOfflinePlayer(allianceOwnerName);
            else allianceOwner = Bukkit.getOfflinePlayer(playerUUID);
            
            lore = replaceInLore(lore, "%OWNER%", allianceOwnerName);
            lore = replaceInLore(lore, "%NAME%", allianceName);
            lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(alliancePrefix));
            lore = replaceInLore(lore, "%DATE%", allianceDate);
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
            lore = replaceInLore(lore, "%MEMBERS%", String.join(", ", allianceMembers));
            lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(allianceDescription));
            
            
            if (plugin.playerHasPermission(playername, team, "seeTeamAlliances")){ 
                item = ItemBuilder.getPlayerHead(allianceOwner, itemName, lore);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    itemName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.teamAlliances").getKeys(false)){
            Integer slot = config.getInt("inventories.teamAlliances."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamAlliances."+str+".itemType"));
            String name = config.getString("inventories.teamAlliances."+str+".itemName");
            String headTexture = config.getString("inventories.teamAlliances."+str+".headTexture");
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, Collections.emptyList(), headTexture, false));
        }
        return inventory;
    }


    public Inventory createEditAlliancesInventory(String playername, Team team) {
        Integer slots = config.getInt("inventoriesSlots.editAlliances");
        String teamName = team.getTeamName();
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.editAlliances"));
        Integer maxMembers = config.getInt("teamMaxMembers");

        emptyCases(inventory, slots, 0);
        Integer headSlot = 0;
        for(Alliance alliance : team.getTeamAlliances()){
            Team teamAlliance = sqlManager.getTeamFromTeamName(alliance.getTeamName());
            String allianceName = teamAlliance.getTeamName();
            String alliancePrefix = teamAlliance.getTeamPrefix();
            String allianceOwnerName = teamAlliance.getTeamOwnerName();
            String allianceDate = alliance.getAllianceDate();
            String allianceDescription = teamAlliance.getTeamDescription();
            Integer teamMembersLelvel = teamAlliance.getImprovLvlMembers();
            Integer teamMembers = teamAlliance.getTeamMembers().size();
            ArrayList<String> allianceMembers = teamAlliance.getMembersName();
            UUID playerUUID = sqliteManager.getUUID(allianceOwnerName);
            String itemName = colorsBuilder.replaceColor(alliancePrefix);
            List<String> lore = config.getStringList("teamAllianceLore");
            OfflinePlayer allianceOwner;
            ItemStack item;


            if (playerUUID == null) allianceOwner = Bukkit.getOfflinePlayer(allianceOwnerName);
            else allianceOwner = Bukkit.getOfflinePlayer(playerUUID);

            
            lore = replaceInLore(lore, "%OWNER%", allianceOwnerName);
            lore = replaceInLore(lore, "%NAME%", teamName);
            lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(alliancePrefix));
            lore = replaceInLore(lore, "%DATE%", allianceDate);
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
            lore = replaceInLore(lore, "%MEMBERS%", String.join(", ", allianceMembers));
            lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(allianceDescription));
            
            
            if (plugin.playerHasPermission(playername, team, "seeAllliances")){ 
                item = ItemBuilder.getPlayerHead(allianceOwner, itemName, lore);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    itemName, 
                    config.getStringList("noPermission.lore"), 
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.editAlliances").getKeys(false)){
            Integer slot = config.getInt("inventories.editAlliances."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.editAlliances."+str+".itemType"));
            String name = config.getString("inventories.editAlliances."+str+".itemName");
            String headTexture = config.getString("inventories.editAlliances."+str+".headTexture");
            ItemStack item;

            if (plugin.playerHasPermission(playername, team, str)){ 
                item = ItemBuilder.getItem(
                    material,
                    name,
                    config.getStringList("inventories.editAlliances."+str+".lore"),
                    headTexture,
                    false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }
            inventory.setItem(slot, item);
        }
        return inventory;
    }


    public Inventory createTeamListInventory(String playerName){
        ArrayList<Team> teams = sqlManager.getTeams();
        Integer slots = config.getInt("inventoriesSlots.teamList");
        List<Integer> headSlots = config.getIntegerList("inventories.teamList.teamOwnerHead.slots");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.teamList"));
        Integer maxMembers = config.getInt("teamMaxMembers");
        Page page;

        if (cacheManager.containsPage(playerName)){
            page = cacheManager.getPage(playerName);
        }else{
            page = new Page(playerName, headSlots.size(), teams);
            cacheManager.addPage(page);
        }
        ArrayList<List<Team>> pages = page.getPages();
        
        emptyCases(inventory, slots, 0);
        Integer slotIndex = 0;
        for(Team team : pages.get(page.getCurrentPage())){
            String teamOwnerName = team.getTeamOwnerName();
            String teamPrefix = team.getTeamPrefix();
            UUID playerUUID = sqliteManager.getUUID(teamOwnerName);
            String itemName = colorsBuilder.replaceColor(teamPrefix);
            List<String> lore = config.getStringList("teamListLore");
            OfflinePlayer teamOwner;
            ItemStack item;

            if (!sqliteManager.isRegistered(teamOwnerName)){
                sqliteManager.registerOfflinePlayer(Bukkit.getOfflinePlayer(teamOwnerName));
            }

            if (playerUUID == null) teamOwner = Bukkit.getOfflinePlayer(teamOwnerName);
            else teamOwner = Bukkit.getOfflinePlayer(playerUUID);
            
            lore = replaceInLore(lore, "%OWNER%", teamOwnerName);
            lore = replaceInLore(lore, "%NAME%", team.getTeamName());
            lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(teamPrefix));
            lore = replaceInLore(lore, "%DATE%", team.getCreationDate());
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(team.getTeamMembers().size()));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+team.getImprovLvlMembers()));
            lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(team.getTeamDescription()));
            
            item = ItemBuilder.getPlayerHead(teamOwner, itemName, lore);
            // item = ItemBuilder.getItem(Material.DIRT, teamOwnerName, lore, null, false);

            inventory.setItem(headSlots.get(slotIndex), item);
            slotIndex ++;
        }

        for(String str : config.getConfigurationSection("inventories.teamList").getKeys(false)){
            if (str.equals("teamOwnerHead")) continue;

            Integer slot = config.getInt("inventories.teamList."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamList."+str+".itemType"));
            String name = config.getString("inventories.teamList."+str+".itemName");
            List<String> lore = config.getStringList("inventories.teamList."+str+".lore");
            String headTexture = config.getString("inventories.teamList."+str+".headTexture");

            if (str.equals("previous")){
                if (page.getCurrentPage() == 0) continue;
            }else if (str.equals("next")){
                if (page.getCurrentPage() == pages.size()-1) continue;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }

        return inventory;
    }


    private List<String> replaceInLore(List<String> lore, String value, String replace){
        List<String> newLore = new ArrayList<>();
        for(String str : lore){
            newLore.add(str.replace(value, replace));
        }
        return newLore;
    }


    private String IntegerToString(int value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


    private String DoubleToString(double value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


	public void emptyCases(Inventory inventory, Integer slots, Integer start) {
		ItemStack item = new ItemStack(Material.getMaterial(config.getString("emptyCase")), 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);

        for(int i = start; i < slots; i++) {
			inventory.setItem(i, item);
		}
	}

    public void emptyCases(Inventory inventory, List<Integer> list) {
		ItemStack item = new ItemStack(Material.getMaterial(config.getString("emptyCase")), 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);

        for(int i : list) {
            inventory.setItem(i, item);
        }
	}
}