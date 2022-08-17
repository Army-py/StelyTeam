package fr.army.stelyteam.utils;

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

public class InventoryBuilder {

    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private SQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private SerializeManager serializeManager;


    public InventoryBuilder(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.serializeManager = new SerializeManager();
    }


	public Inventory createTeamInventory() {
		Integer slots = config.getInt("inventoriesSlots.createTeam");
		Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.createTeam"));
		
        emptyCases(inventory, slots);
		
        Integer slot = config.getInt("inventories.createTeam.slot");
        Material material = Material.getMaterial(config.getString("inventories.createTeam.itemType"));
        String name = config.getString("inventories.createTeam.itemName");
        List<String> lore = config.getStringList("inventories.createTeam.lore");

        inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
		
		return inventory;
	}


    public Inventory createAdminInventory() {
        Integer slots = config.getInt("inventoriesSlots.admin");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.admin"));

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.admin").getKeys(false)){
            Integer slot = config.getInt("inventories.admin."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.admin."+str+".itemType"));
            String name = config.getString("inventories.admin."+str+".itemName");
            List<String> lore = config.getStringList("inventories.admin."+str+".lore");
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public Inventory createManageInventory(String playername) {
        Integer slots = config.getInt("inventoriesSlots.manage");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.manage"));
        String teamId = sqlManager.getTeamIDFromPlayer(playername);

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.manage").getKeys(false)){
            Integer slot = config.getInt("inventories.manage."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.manage."+str+".itemType"));
            String name = config.getString("inventories.manage."+str+".itemName");
            List<String> lore = config.getStringList("inventories.manage."+str+".lore");
            ItemStack item;
            
            if (playerHasPermission(playername, teamId, str)){ 
                if (str.equals("buyTeamBank")){
                    item = ItemBuilder.getItem(material, name, lore, sqlManager.hasUnlockedTeamBank(teamId));
                }else {
                    item = ItemBuilder.getItem(material, name, lore, false);
                }
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"), 
                    false
                );
            }

            inventory.setItem(slot,  item);
        }
        return inventory;
    }


    public Inventory createMemberInventory(String playername) {
        Integer slots = config.getInt("inventoriesSlots.member");
        String teamID = sqlManager.getTeamIDFromPlayer(playername);
        String teamPrefix = sqlManager.getTeamPrefix(teamID);
        String teamOwner = sqlManager.getTeamOwner(teamID);
        Integer teamMembersLelvel = sqlManager.getTeamMembersLevel(teamID);
        Integer teamMembers = sqlManager.getMembers(teamID).size();
        Integer maxMembers = config.getInt("teamMaxMembers");
        String memberJoinDate = sqlManager.getJoinDate(playername);
        String memberRank = plugin.getRankFromId(sqlManager.getMemberRank(playername));
        String memberRankName = config.getString("ranks." + memberRank + ".name");
        String rankColor = config.getString("ranks." + memberRank + ".color");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.member"));
        Double teamMoney = sqlManager.getTeamMoney(teamID);

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.member").getKeys(false)){
            Integer slot = config.getInt("inventories.member."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.member."+str+".itemType"));
            String name = config.getString("inventories.member."+str+".itemName");
            List<String> lore = config.getStringList("inventories.member."+str+".lore");
            ItemStack item;

            if (name.equals(config.getString("inventories.member.seeTeamBank.itemName"))){
                lore = replaceInLore(lore, "%TEAM_MONEY%", DoubleToString(teamMoney));
                lore = replaceInLore(lore, "%MAX_MONEY%", DoubleToString(config.getDouble("teamMaxMoney")));
            }else if (name.equals(config.getString("inventories.member.teamInfos.itemName"))){
                lore = replaceInLore(lore, "%NAME%", teamID);
                lore = replaceInLore(lore, "%PREFIX%", new ColorsBuilder().replaceColor(teamPrefix));
                lore = replaceInLore(lore, "%OWNER%", teamOwner);
                lore = replaceInLore(lore, "%RANK%", rankColor + memberRankName);
                lore = replaceInLore(lore, "%DATE%", memberJoinDate);
                lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
                lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
            }

            if (playerHasPermission(playername, teamID, str)){ 
                item = ItemBuilder.getItem(material, name, lore, false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"), 
                    false
                );
            }

            if (name.equals(config.getString("inventories.member.seeTeamBank.itemName"))){
                if (!sqlManager.hasUnlockedTeamBank(teamID)){
                    item = ItemBuilder.getItem(
                        Material.getMaterial(config.getString("teamBankNotUnlock.itemType")),
                        config.getString("teamBankNotUnlock.itemName"),
                        Collections.emptyList(),
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

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.confirmInventory").getKeys(false)){
            Material material = Material.getMaterial(config.getString("inventories.confirmInventory."+str+".itemType"));
            String name = config.getString("inventories.confirmInventory."+str+".itemName");
            List<String> lore = config.getStringList("inventories.confirmInventory."+str+".lore");

            for(Integer slot : config.getIntegerList("inventories.confirmInventory."+str+".slots")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    public Inventory createUpgradeTotalMembersInventory(String playername) {
        Integer slots = config.getInt("inventoriesSlots.upgradeTotalMembers");
        String teamID = sqlManager.getTeamIDFromPlayer(playername);
        Integer level = sqlManager.getTeamMembersLevel(teamID);
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.upgradeTotalMembers"));

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeTotalMembers."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.upgradeTotalMembers."+str+".itemType"));
            String name = config.getString("inventories.upgradeTotalMembers."+str+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeTotalMembers."+str+".lore");

            if (level >= config.getInt("inventories.upgradeTotalMembers."+str+".level") && !str.equals("close")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, true));
            }else{
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    public Inventory createUpgradeStorageInventory(String playername) {
        Integer slots = config.getInt("inventoriesSlots.upgradeStorageAmount");
        String teamID = sqlManager.getTeamIDFromPlayer(playername);
        Integer level = sqlManager.getTeamStorageLevel(teamID);
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.upgradeStorageAmount"));

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.upgradeStorageAmount").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeStorageAmount."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.upgradeStorageAmount."+str+".itemType"));
            String name = config.getString("inventories.upgradeStorageAmount."+str+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeStorageAmount."+str+".lore");

            if (level >= config.getInt("inventories.upgradeStorageAmount."+str+".level") && !str.equals("close")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, true));
            }else{
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    public Inventory createMembersInventory(String playername) {
        Integer slots = config.getInt("inventoriesSlots.teamMembers");
        String teamID = sqlManager.getTeamIDFromPlayer(playername);
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.teamMembers"));

        emptyCases(inventory, slots);
        Integer headSlot = 0;
        for(String str : sqlManager.getMembers(teamID)){
            UUID playerUUID = sqliteManager.getUUID(str);
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer member;

            if (playerUUID == null) member = Bukkit.getOfflinePlayer(str);
            else member = Bukkit.getOfflinePlayer(playerUUID);

            String memberRank = plugin.getRankFromId(sqlManager.getMemberRank(str));
            String rankColor = config.getString("ranks." + memberRank + ".color");
            itemName = rankColor + str;
            
            lore.add(config.getString("prefixRankLore") + rankColor + config.getString("ranks." + memberRank + ".name"));
            inventory.setItem(headSlot, ItemBuilder.getPlayerHead(member, itemName, lore));
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.teamMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.teamMembers."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamMembers."+str+".itemType"));
            String name = config.getString("inventories.teamMembers."+str+".itemName");
            List<String> lore;

            if (sqlManager.isOwner(playername)) lore = config.getStringList("inventories.teamMembers."+str+".lore");
            else lore = Collections.emptyList();
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public Inventory createEditMembersInventory(String playername) {
        Integer slots = config.getInt("inventoriesSlots.editMembers");
        String teamID = sqlManager.getTeamIDFromPlayer(playername);
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.editMembers"));

        emptyCases(inventory, slots);
        Integer headSlot = 0;
        for(String str : sqlManager.getMembers(teamID)){
            UUID playerUUID = sqliteManager.getUUID(str);
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer member;
            ItemStack item;

            if (playerUUID == null) member = Bukkit.getOfflinePlayer(str);
            else member = Bukkit.getOfflinePlayer(playerUUID);

            Integer memberRank = sqlManager.getMemberRank(str);
            String memberRankName = plugin.getRankFromId(memberRank);
            String rankColor = config.getString("ranks." + memberRankName + ".color");
            itemName = rankColor + str;
            
            if (!sqlManager.isOwner(str)){
                lore = config.getStringList("editMembersLores");
                if (plugin.getLastRank() == memberRank) lore.remove(1);
            }

            lore.add(0, config.getString("prefixRankLore") + rankColor + config.getString("ranks." + memberRankName + ".name"));
            
            if (playerHasPermission(playername, teamID, "manageMembers")){ 
                item = ItemBuilder.getPlayerHead(member, itemName, lore);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    itemName, 
                    config.getStringList("noPermission.lore"), 
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
            List<String> lore;
            ItemStack item;

            if (sqlManager.isOwner(playername)) lore = config.getStringList("inventories.editMembers."+str+".lore");
            else lore = Collections.emptyList();
            

            if (playerHasPermission(playername, teamID, str)){ 
                item = ItemBuilder.getItem(material, name, lore, false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"), 
                    false
                );
            }
            inventory.setItem(slot, item);
        }
        return inventory;
    }


    public Inventory createPermissionsInventory(String playerName) {
        Integer slots = config.getInt("inventoriesSlots.permissions");
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.permissions"));
        String teamId = sqlManager.getTeamIDFromPlayer(playerName);

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.permissions").getKeys(false)){
            Integer slot = config.getInt("inventories.permissions."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.permissions."+str+".itemType"));
            String name = config.getString("inventories.permissions."+str+".itemName");
            List<String> lore = config.getStringList("inventories.permissions."+str+".lore");

            String rankPath = config.getString("inventories.permissions."+str+".rankPath");
            Integer defaultRankId = config.getInt("inventories."+rankPath+".rank");
            Integer permissionRank = sqlManager.getPermissionRank(teamId, str);
            String lorePrefix = config.getString("prefixRankLore");

            if (permissionRank != null){
                String rankColor = config.getString("ranks." + plugin.getRankFromId(permissionRank) + ".color");
                lore.add(0, lorePrefix + rankColor + config.getString("ranks." + plugin.getRankFromId(permissionRank) + ".name"));
            }else{
                if (rankPath != null){
                    String rankColor = config.getString("ranks." + plugin.getRankFromId(defaultRankId) + ".color");
                    lore.add(0, lorePrefix + rankColor + config.getString("ranks." + plugin.getRankFromId(defaultRankId) + ".name"));
                }
            }

            boolean isDefault = false;
            if (!str.equals("close") && (permissionRank == null || defaultRankId == permissionRank)){
                isDefault = true;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, isDefault));
        }
        return inventory;
    }


    public Inventory createStorageDirectoryInventory(String playerName) {
        Integer slots = config.getInt("inventoriesSlots.storageDirectory");
        String teamId = sqlManager.getTeamIDFromPlayer(playerName);
        Inventory inventory = Bukkit.createInventory(null, slots, config.getString("inventoriesName.storageDirectory"));
        Integer level = sqlManager.getTeamStorageLevel(teamId);

        emptyCases(inventory, slots);

        for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            Integer slot = config.getInt("inventories.storageDirectory."+str+".slot");
            Material material;
            String name;
            List<String> lore;

            if (level >= config.getInt("inventories.storageDirectory."+str+".level") || str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.storageDirectory."+str+".itemType"));
                if (str.equals("close")){
                    name = config.getString("inventories.storageDirectory."+str+".itemName");
                }else{
                    name = config.getString(config.getString("inventories.storageDirectory."+str+".itemName"));
                }
                lore = config.getStringList("inventories.storageDirectory."+str+".lore");

                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }else{
                material = Material.getMaterial(config.getString("storageNotUnlock.itemType"));
                name = config.getString("storageNotUnlock.itemName");
                lore = config.getStringList("storageNotUnlock.lore");

                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    public Inventory createStorageInventory(String teamId, String storageId, String storageName){
        Integer slots = config.getInt("inventoriesSlots.storage");
        Inventory inventory;

        if (plugin.containTeamStorage(teamId, storageId)){
            inventory = plugin.getStorageInstance(teamId, storageId);
        }else{
            inventory = Bukkit.createInventory(null, slots, storageName);
            plugin.addTeamStorage(teamId, inventory, storageId, serializeManager.serialize(inventory.getContents()));

            if (sqlManager.teamHasStorage(teamId, storageId)){
                String contentString = sqlManager.getStorageContent(teamId, storageId);
                ItemStack[] content = serializeManager.deserialize(contentString);
                inventory.setContents(content);
            }
        }


        for(String str : config.getConfigurationSection("inventories.storage").getKeys(false)){
            Integer slot = config.getInt("inventories.storage."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.storage."+str+".itemType"));
            String name = config.getString("inventories.storage."+str+".itemName");
            List<String> lore = config.getStringList("inventories.storage."+str+".lore");

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
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


    private boolean playerHasPermission(String playerName, String teamId, String permission){
        Integer permissionRank = sqlManager.getPermissionRank(teamId, permission);
        if (permissionRank != null){
            return permissionRank >= sqlManager.getMemberRank(playerName);
        }

        String rankPath = config.getString("inventories.permissions."+permission+".rankPath");
        if (sqlManager.isOwner(playerName) || config.getInt("inventories."+rankPath+".rank") == -1){
            return true;
        }else if (config.getInt("inventories."+rankPath+".rank") >= sqlManager.getMemberRank(playerName)){
            return true;
        }else if (permission.equals("close") || permission.equals("editMembers") || permission.equals("leaveTeam") || permission.equals("teamInfos")){
            return true;
        }
        return false;
    }


	public void emptyCases(Inventory inventory, Integer slots) {
		ItemStack item = new ItemStack(Material.getMaterial(config.getString("emptyCase")), 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);

        for(int i = 0; i < slots; i++) {
			inventory.setItem(i, item);
		}
	}
}