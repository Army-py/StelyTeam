package fr.army.utils;

import java.util.HashMap;

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

	HashMap<Integer, ItemStack> items;

	
	public static Inventory createTeamInventory() {
		Integer slots = 27;
		Inventory inventory = Bukkit.createInventory(null, slots, App.config.getString("inventoriesName.createTeam"));
		
        emptyCases(inventory, slots);
		
        Integer slot = App.config.getInt("createTeam.slot");
        Material material = Material.getMaterial(App.config.getString("createTeam.itemType"));
        String name = App.config.getString("createTeam.itemName");

        inventory.setItem(slot, ItemBuilder.getItem(material, name));
		
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
