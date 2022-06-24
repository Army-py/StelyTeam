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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.army.stelyteam.StelyTeamPlugin;

public class InventoryGenerator {	
	public static Inventory createTeamInventory() {
		Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.createTeam");
		Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.createTeam"));
		
        emptyCases(inventory, slots);
		
        Integer slot = StelyTeamPlugin.config.getInt("inventories.createTeam.slot");
        Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.createTeam.itemType"));
        String name = StelyTeamPlugin.config.getString("inventories.createTeam.itemName");
        List<String> lore = StelyTeamPlugin.config.getStringList("inventories.createTeam.lore");

        inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
		
		return inventory;
	}


    public static Inventory createAdminInventory() {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.admin");
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.admin"));

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.admin").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.admin."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.admin."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.admin."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.admin."+str+".lore");
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public static Inventory createManageInventory(String playername) {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.manage");
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.manage"));
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.manage").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.manage."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.manage."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.manage."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.manage."+str+".lore");
            ItemStack item;
            
            if (playerHasPermission(playername, teamId, str)){ 
                if (str.equals("buyTeamBank")){
                    item = ItemBuilder.getItem(material, name, lore, StelyTeamPlugin.sqlManager.hasUnlockedTeamBank(teamId));
                }else {
                    item = ItemBuilder.getItem(material, name, lore, false);
                }
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(StelyTeamPlugin.config.getString("noPermission.itemType")), 
                    name, 
                    StelyTeamPlugin.config.getStringList("noPermission.lore"), 
                    false
                );
            }

            inventory.setItem(slot,  item);
        }
        return inventory;
    }


    public static Inventory createMemberInventory(String playername) {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.member");
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);
        String teamPrefix = StelyTeamPlugin.sqlManager.getTeamPrefix(teamID);
        String teamOwner = StelyTeamPlugin.sqlManager.getTeamOwner(teamID);
        Integer teamMembersLelvel = StelyTeamPlugin.sqlManager.getTeamLevel(teamID);
        Integer teamMembers = StelyTeamPlugin.sqlManager.getMembers(teamID).size();
        Integer maxMembers = StelyTeamPlugin.config.getInt("teamMaxMembers");
        String memberRank = StelyTeamPlugin.getRankFromId(StelyTeamPlugin.sqlManager.getMemberRank(playername));
        String memberRankName = StelyTeamPlugin.config.getString("ranks." + memberRank + ".name");
        String rankColor = StelyTeamPlugin.config.getString("ranks." + memberRank + ".color");
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.member"));
        Float teamMoney = StelyTeamPlugin.sqlManager.getTeamMoney(teamID);

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.member").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.member."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.member."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.member."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.member."+str+".lore");
            ItemStack item;
            
            if (name.equals(StelyTeamPlugin.config.getString("inventories.member.seeTeamBank.itemName"))){
                lore = replaceInLore(lore, "%teamMoney%", FloatToString(teamMoney));
            }else if (name.equals(StelyTeamPlugin.config.getString("inventories.member.teamInfos.itemName"))){
                lore = replaceInLore(lore, "%NAME%", teamID);
                lore = replaceInLore(lore, "%PREFIX%", new ColorsBuilder().replaceColor(teamPrefix));
                lore = replaceInLore(lore, "%OWNER%", teamOwner);
                lore = replaceInLore(lore, "%RANK%", rankColor + memberRankName);
                lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
                lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
            }

            if (playerHasPermission(playername, teamID, str)){ 
                item = ItemBuilder.getItem(material, name, lore, false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(StelyTeamPlugin.config.getString("noPermission.itemType")), 
                    name, 
                    StelyTeamPlugin.config.getStringList("noPermission.lore"), 
                    false
                );
            }
            inventory.setItem(slot,  item);
        }
        return inventory;
    }


    public static Inventory createConfirmInventory() {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.confirmInventory");
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.confirmInventory"));

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.confirmInventory").getKeys(false)){
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.confirmInventory."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.confirmInventory."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.confirmInventory."+str+".lore");

            for(Integer slot : StelyTeamPlugin.config.getIntegerList("inventories.confirmInventory."+str+".slots")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    public static Inventory createUpgradeTotalMembersInventory(String playername) {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.upgradeTotalMembers");
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);
        Integer level = StelyTeamPlugin.sqlManager.getTeamLevel(teamID);
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.upgradeTotalMembers"));

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.upgradeTotalMembers."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.upgradeTotalMembers."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.upgradeTotalMembers."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.upgradeTotalMembers."+str+".lore");

            if (level >= StelyTeamPlugin.config.getInt("inventories.upgradeTotalMembers."+str+".level") && !str.equals("close")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, true));
            }else{
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    public static Inventory createMembersInventory(String playername) {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.teamMembers");
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.teamMembers"));

        emptyCases(inventory, slots);
        Integer headSlot = 0;
        for(String str : StelyTeamPlugin.sqlManager.getMembers(teamID)){
            UUID playerUUID = StelyTeamPlugin.sqliteManager.getUUID(str);
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer member;

            if (playerUUID == null) member = Bukkit.getOfflinePlayer(str);
            else member = Bukkit.getOfflinePlayer(playerUUID);

            String memberRank = StelyTeamPlugin.getRankFromId(StelyTeamPlugin.sqlManager.getMemberRank(str));
            String rankColor = StelyTeamPlugin.config.getString("ranks." + memberRank + ".color");
            itemName = rankColor + str;
            
            lore.add("§r§5> " + rankColor + StelyTeamPlugin.config.getString("ranks." + memberRank + ".name"));
            inventory.setItem(headSlot, ItemBuilder.getPlayerHead(member, itemName, lore));
            headSlot ++;
        }

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.teamMembers").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.teamMembers."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.teamMembers."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.teamMembers."+str+".itemName");
            List<String> lore;

            if (StelyTeamPlugin.sqlManager.isOwner(playername)) lore = StelyTeamPlugin.config.getStringList("inventories.teamMembers."+str+".lore");
            else lore = Collections.emptyList();
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public static Inventory createEditMembersInventory(String playername) {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.editMembers");
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.editMembers"));

        emptyCases(inventory, slots);
        Integer headSlot = 0;
        for(String str : StelyTeamPlugin.sqlManager.getMembers(teamID)){
            UUID playerUUID = StelyTeamPlugin.sqliteManager.getUUID(str);
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer member;
            ItemStack item;

            if (playerUUID == null) member = Bukkit.getOfflinePlayer(str);
            else member = Bukkit.getOfflinePlayer(playerUUID);

            Integer memberRank = StelyTeamPlugin.sqlManager.getMemberRank(str);
            String memberRankName = StelyTeamPlugin.getRankFromId(memberRank);
            String rankColor = StelyTeamPlugin.config.getString("ranks." + memberRankName + ".color");
            itemName = rankColor + str;
            
            if (!StelyTeamPlugin.sqlManager.isOwner(str)){
                lore = StelyTeamPlugin.config.getStringList("editMembersLores");
                if (StelyTeamPlugin.getLastRank() == memberRank) lore.remove(1);
            }

            lore.add(0, "§r§5> " + rankColor + StelyTeamPlugin.config.getString("ranks." + memberRankName + ".name"));
            
            if (playerHasPermission(playername, teamID, "manageMembers")){ 
                item = ItemBuilder.getPlayerHead(member, itemName, lore);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(StelyTeamPlugin.config.getString("noPermission.itemType")), 
                    itemName, 
                    StelyTeamPlugin.config.getStringList("noPermission.lore"), 
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.editMembers").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.editMembers."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.editMembers."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.editMembers."+str+".itemName");
            List<String> lore;
            ItemStack item;

            if (StelyTeamPlugin.sqlManager.isOwner(playername)) lore = StelyTeamPlugin.config.getStringList("inventories.editMembers."+str+".lore");
            else lore = Collections.emptyList();
            

            if (playerHasPermission(playername, teamID, str)){ 
                item = ItemBuilder.getItem(material, name, lore, false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(StelyTeamPlugin.config.getString("noPermission.itemType")), 
                    name, 
                    StelyTeamPlugin.config.getStringList("noPermission.lore"), 
                    false
                );
            }
            inventory.setItem(slot, item);
        }
        return inventory;
    }


    public static Inventory createPermissionsInventory(String playerName) {
        Integer slots = StelyTeamPlugin.config.getInt("inventoriesSlots.permissions");
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.permissions"));
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.permissions").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.permissions."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.permissions."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.permissions."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.permissions."+str+".lore");

            String rankPath = StelyTeamPlugin.config.getString("inventories.permissions."+str+".rankPath");
            Integer defaultRankId = StelyTeamPlugin.config.getInt("inventories."+rankPath+".rank");
            Integer permissionRank = StelyTeamPlugin.sqlManager.getPermissionRank(teamId, str);

            if (permissionRank != null){
                String rankColor = StelyTeamPlugin.config.getString("ranks." + StelyTeamPlugin.getRankFromId(permissionRank) + ".color");
                lore.add(0, "§r§5> " + rankColor + StelyTeamPlugin.config.getString("ranks." + StelyTeamPlugin.getRankFromId(permissionRank) + ".name"));
            }else{
                if (rankPath != null){
                    String rankColor = StelyTeamPlugin.config.getString("ranks." + StelyTeamPlugin.getRankFromId(defaultRankId) + ".color");
                    lore.add(0, "§r§5> " + rankColor + StelyTeamPlugin.config.getString("ranks." + StelyTeamPlugin.getRankFromId(defaultRankId) + ".name"));
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


    private static List<String> replaceInLore(List<String> lore, String value, String replace){
        List<String> newLore = new ArrayList<>();
        for(String str : lore){
            newLore.add(str.replace(value, replace));
        }
        return newLore;
    }


    private static String IntegerToString(Integer value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


    private static String FloatToString(Float value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


    private static boolean playerHasPermission(String playerName, String teamId, String permission){
        Integer permissionRank = StelyTeamPlugin.sqlManager.getPermissionRank(teamId, permission);
        if (permissionRank != null){
            return permissionRank >= StelyTeamPlugin.sqlManager.getMemberRank(playerName);
        }

        String rankPath = StelyTeamPlugin.config.getString("inventories.permissions."+permission+".rankPath");
        if (StelyTeamPlugin.sqlManager.isOwner(playerName) || StelyTeamPlugin.config.getInt("inventories."+rankPath+".rank") == -1){
            return true;
        }else if (StelyTeamPlugin.config.getInt("inventories."+rankPath+".rank") >= StelyTeamPlugin.sqlManager.getMemberRank(playerName)){
            return true;
        }else if (permission.equals("close") || permission.equals("editMembers") || permission.equals("leaveTeam")){
            return true;
        }
        return false;
    }


	public static void emptyCases(Inventory inventory, Integer slots) {
		ItemStack item = new ItemStack(Material.getMaterial(StelyTeamPlugin.config.getString("emptyCase")), 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);

        for(int i = 0; i < slots; i++) {
			if(i != 11 || i != 15) {
				inventory.setItem(i, item);
			}
		}
	}
}