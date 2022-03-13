package fr.army.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.army.App;

public class InventoryGenerator {
    Player player;
	String nom;
	Inventory inventaire;

	
	public static Inventory createTeamInventory() {
		Integer slots = 27;
		Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.createTeam"));
		
        emptyCases(inventory, slots);
		
        Integer slot = App.config.getInt("createTeam.slot");
        Material material = Material.getMaterial(App.config.getString("createTeam.itemType"));
        String name = App.config.getString("createTeam.itemName");
        List<String> lore = App.config.getStringList("createTeam.lore");

        inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
		
		return inventory;
	}


    public static Inventory createAdminInventory() {
        Integer slots = 27;
        Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.admin"));

        emptyCases(inventory, slots);

        for(String str : App.config.getConfigurationSection("admin").getKeys(false)){
            Integer slot = App.config.getInt("admin."+str+".slot");
            Material material = Material.getMaterial(App.config.getString("admin."+str+".itemType"));
            String name = App.config.getString("admin."+str+".itemName");
            List<String> lore = App.config.getStringList("admin."+str+".lore");
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public static Inventory createManageInventory(String playername) {
        Integer slots = 54;
        Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.manage"));

        emptyCases(inventory, slots);

        for(String str : App.config.getConfigurationSection("manage").getKeys(false)){
            Integer slot = App.config.getInt("manage."+str+".slot");
            Material material = Material.getMaterial(App.config.getString("manage."+str+".itemType"));
            String name = App.config.getString("manage."+str+".itemName");
            List<String> lore = App.config.getStringList("manage."+str+".lore");
            ItemStack item = ItemBuilder.getItem(material, name, lore, false);
            
            if (str.equals("teamBank")){
                String teamID = App.sqlManager.getTeamIDFromPlayer(playername);
                item = ItemBuilder.getItem(material, name, lore, App.sqlManager.hasUnlockedTeamBank(teamID));
            }
            

            if (App.sqlManager.isOwner(playername)){
                inventory.setItem(slot, item);
            }
            if (App.config.getInt("manage."+str+".rank") == 1){
                inventory.setItem(slot,  item);
            }
        }
        return inventory;
    }


    public static Inventory createMemberInventory() {
        Integer slots = 27;
        Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.member"));

        emptyCases(inventory, slots);

        for(String str : App.config.getConfigurationSection("member").getKeys(false)){
            Integer slot = App.config.getInt("member."+str+".slot");
            Material material = Material.getMaterial(App.config.getString("member."+str+".itemType"));
            String name = App.config.getString("member."+str+".itemName");
            List<String> lore = App.config.getStringList("member."+str+".lore");
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
        }
        return inventory;
    }


    public static Inventory createConfirmInventory() {
        Integer slots = 27;
        Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.confirmInventory"));

        emptyCases(inventory, slots);

        for(String str : App.config.getConfigurationSection("confirmInventory").getKeys(false)){
            Material material = Material.getMaterial(App.config.getString("confirmInventory."+str+".itemType"));
            String name = App.config.getString("confirmInventory."+str+".itemName");
            List<String> lore = App.config.getStringList("confirmInventory."+str+".lore");

            for(Integer slot : App.config.getIntegerList("confirmInventory."+str+".slots")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    public static Inventory createUpgradeMembersInventory(String playername) {
        Integer slots = 27;
        String teamID = App.sqlManager.getTeamIDFromPlayer(playername);
        Integer level = App.sqlManager.getTeamLevel(teamID);
        Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.upgradeTotalMembers"));

        emptyCases(inventory, slots);

        for(String str : App.config.getConfigurationSection("upgradeTotalMembers").getKeys(false)){
            Integer slot = App.config.getInt("upgradeTotalMembers."+str+".slot");
            Material material = Material.getMaterial(App.config.getString("upgradeTotalMembers."+str+".itemType"));
            String name = App.config.getString("upgradeTotalMembers."+str+".itemName");
            List<String> lore = App.config.getStringList("upgradeTotalMembers."+str+".lore");

            if (level >= App.config.getInt("upgradeTotalMembers."+str+".level") && !str.equals("close")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, true));
            }else{
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, false));
            }
        }
        return inventory;
    }


    // public static Inventory createMembersHeadInventory


	public static void emptyCases(Inventory inventory, Integer slots) {
		ItemStack item = new ItemStack(Material.getMaterial(App.config.getString("emptyCase")), 1);
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