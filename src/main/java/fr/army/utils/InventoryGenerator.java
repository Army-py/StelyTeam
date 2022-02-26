package fr.army.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        List<String> lore = new ArrayList<>();

        Integer createTeamPrice = App.config.getInt("prices.createTeam");

        for (String loreLine : App.config.getStringList("createTeam.lore")) {
            lore.add(loreLine.replace("%price%", NumberFormat.getNumberInstance(Locale.US).format(createTeamPrice)));
        }

        inventory.setItem(slot, ItemBuilder.getItem(material, name, lore));
		
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
            if(App.config.getStringList("admin."+str+".lore").size() > 0){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, App.config.getStringList("admin."+str+".lore")));
            }else{
                inventory.setItem(slot, ItemBuilder.getItem(material, name, null));
            }
        }
        return inventory;
    }


    public static Inventory createManageInventory(String playername) {
        Integer slots = 54;
        Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.manage"));

        emptyCases(inventory, slots);

        for(String str : App.config.getConfigurationSection("manage").getKeys(false)){
            Integer slot = App.config.getInt("manage."+str+".slot");
            if (App.sqlManager.isOwner(playername)){
                Material material = Material.getMaterial(App.config.getString("manage."+str+".itemType"));
                String name = App.config.getString("manage."+str+".itemName");
                if(App.config.getStringList("manage."+str+".lore").size() > 0){
                    inventory.setItem(slot, ItemBuilder.getItem(material, name, App.config.getStringList("manage."+str+".lore")));
                }else{
                    inventory.setItem(slot, ItemBuilder.getItem(material, name, null));
                }
            }else if (App.config.getInt("manage."+str+".rank") == 1 && App.sqlManager.isAdmin(playername)){
                Material material = Material.getMaterial(App.config.getString("manage."+str+".itemType"));
                String name = App.config.getString("manage."+str+".itemName");
                if(App.config.getStringList("manage."+str+".lore").size() > 0){
                    inventory.setItem(slot, ItemBuilder.getItem(material, name, App.config.getStringList("manage."+str+".lore")));
                }else{
                    inventory.setItem(slot, ItemBuilder.getItem(material, name, null));
                }
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
            if(App.config.getStringList("member."+str+".lore").size() > 0){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, App.config.getStringList("member."+str+".lore")));
            }else{
                inventory.setItem(slot, ItemBuilder.getItem(material, name, null));
            }
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

            for(Integer slot : App.config.getIntegerList("confirmInventory."+str+".slots")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, null));
            }
        }
        return inventory;
    }

	// public void createParticleInventory() {
	// 	Integer slots = App.config.getInt("inventories.particles.slots");
	// 	Inventory inventaire = Bukkit.createInventory(null, slots, nom);
	// 	this.inventaire = inventaire;
	// 	for(int i = 0; i < slots; i++) {
	// 		inventaire.setItem(i, vitredeco());
	// 	}

	// 	for(String str : App.config.getConfigurationSection("particles").getKeys(false)){
	// 		Integer slot = App.config.getInt("particles."+str+".slot");
	// 		Material material = Material.getMaterial(App.config.getString("particles."+str+".itemType"));
	// 		String name = App.config.getString("particles."+str+".itemName");

	// 		inventaire.setItem(slot, ItemBuilder.getItem(material, name));
	// 	}
	// 	player.openInventory(inventaire);
	// }


	// public void createSoundInventory() {
	// 	Integer slots = App.config.getInt("inventories.sounds.slots");
	// 	Inventory inventaire = Bukkit.createInventory(null, slots, nom);
	// 	this.inventaire = inventaire;
	// 	for(int i = 0; i < slots; i++) {
	// 		inventaire.setItem(i, vitredeco());
	// 	}
		
	// 	for(String str : App.config.getConfigurationSection("sounds").getKeys(false)){
	// 		Integer slot = App.config.getInt("sounds."+str+".slot");
	// 		Material material = Material.getMaterial(App.config.getString("sounds."+str+".itemType"));
	// 		String name = App.config.getString("sounds."+str+".itemName");

	// 		inventaire.setItem(slot, ItemBuilder.getItem(material, name));
	// 	}
	// 	player.openInventory(inventaire);
	// }

	
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