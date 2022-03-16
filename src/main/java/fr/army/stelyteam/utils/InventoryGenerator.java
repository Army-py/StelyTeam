package fr.army.stelyteam.utils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.army.stelyteam.StelyTeamPlugin;

public class InventoryGenerator {
    Player player;
	String nom;
	Inventory inventaire;

	
	public static Inventory createTeamInventory() {
		Integer slots = 27;
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
        Integer slots = 27;
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
        Integer slots = 54;
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.manage"));

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.manage").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.manage."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.manage."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.manage."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.manage."+str+".lore");
            ItemStack item = ItemBuilder.getItem(material, name, lore, false);
            
            if (str.equals("teamBank")){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);
                item = ItemBuilder.getItem(material, name, lore, StelyTeamPlugin.sqlManager.hasUnlockedTeamBank(teamID));
            }
            

            if (StelyTeamPlugin.sqlManager.isOwner(playername)){
                inventory.setItem(slot, item);
            }
            if (StelyTeamPlugin.config.getInt("inventories.manage."+str+".rank") == 1){
                inventory.setItem(slot,  item);
            }
        }
        return inventory;
    }


    public static Inventory createMemberInventory() {
        Integer slots = 27;
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.member"));

        emptyCases(inventory, slots);

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.member").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.member."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.member."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.member."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.member."+str+".lore");
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public static Inventory createConfirmInventory() {
        Integer slots = 27;
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
        Integer slots = 27;
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
        Integer slots = 27;
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.teamMembers"));

        emptyCases(inventory, slots);
        Integer headSlot = 0;
        for(String str : StelyTeamPlugin.sqlManager.getMembers(teamID)){
            UUID playerUUID = StelyTeamPlugin.sqliteManager.getUUID(str);
            
            // inventory.setItem(headSlot, ItemBuilder.getPlayerHead(Bukkit.getOfflinePlayer(playerUUID), null));
            headSlot ++;
        }

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.teamMembers").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.teamMembers."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.teamMembers."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.teamMembers."+str+".itemName");
            List<String> lore = StelyTeamPlugin.config.getStringList("inventories.teamMembers."+str+".lore");
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public static Inventory createEditMembersInventory(String playername) {
        Integer slots = 36;
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playername);
        Inventory inventory = Bukkit.createInventory(null, slots, StelyTeamPlugin.config.getString("inventoriesName.editMembers"));

        emptyCases(inventory, slots);
        Integer headSlot = 0;
        for(String str : StelyTeamPlugin.sqlManager.getMembers(teamID)){
            UUID playerUUID = StelyTeamPlugin.sqliteManager.getUUID(str);
            String itemName;
            List<String> lore;
            OfflinePlayer member;

            if (playerUUID == null){ 
                member = Bukkit.getOfflinePlayer(str);
                if (member == null) continue;
            }else member = Bukkit.getOfflinePlayer(playerUUID);

            if (StelyTeamPlugin.sqlManager.isOwner(str)){
                lore = StelyTeamPlugin.config.getStringList("editMembersLores.owner");
                itemName = StelyTeamPlugin.config.getString("rankColors.owner")+str;
            }else if (StelyTeamPlugin.sqlManager.isAdmin(str)){
                lore = StelyTeamPlugin.config.getStringList("editMembersLores.admin");
                itemName = StelyTeamPlugin.config.getString("rankColors.admin")+str;
            }else{
                lore = StelyTeamPlugin.config.getStringList("editMembersLores.member");
                itemName = StelyTeamPlugin.config.getString("rankColors.member")+str;
            }
            
            inventory.setItem(headSlot, ItemBuilder.getPlayerHead(member, itemName, lore));
            headSlot ++;
        }

        for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.editMembers").getKeys(false)){
            Integer slot = StelyTeamPlugin.config.getInt("inventories.editMembers."+str+".slot");
            Material material = Material.getMaterial(StelyTeamPlugin.config.getString("inventories.editMembers."+str+".itemType"));
            String name = StelyTeamPlugin.config.getString("inventories.editMembers."+str+".itemName");
            List<String> lore;

            if (StelyTeamPlugin.sqlManager.isOwner(playername)) lore = StelyTeamPlugin.config.getStringList("inventories.editMembers."+str+".lore");
            else lore = Collections.emptyList();
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
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