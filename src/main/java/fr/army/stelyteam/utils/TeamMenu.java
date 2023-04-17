package fr.army.stelyteam.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.army.stelyteam.StelyTeamPlugin;


public abstract class TeamMenu implements TeamMenuInterface {

    protected final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    protected final YamlConfiguration config;

    protected InventoryHolder menu;

    protected final Player viewer;
    protected final String menuName;
    protected final int menuSlots;


    public TeamMenu(Player viewer, String menuName, int menuSlots) {
        this.config = plugin.getConfig();
        
        this.viewer = viewer;
        this.menuName = menuName;
        this.menuSlots = menuSlots;
    }

    public TeamMenu(Player viewer, int menuSlots) {
        this.config = plugin.getConfig();
        
        this.viewer = viewer;
        this.menuName = null;
        this.menuSlots = menuSlots;
    }


    public abstract void onClick(InventoryClickEvent clickEvent);

    public abstract void onClose(InventoryCloseEvent closeEvent);


    protected void open(Inventory inventory){
        this.viewer.openInventory(inventory);
    }

    protected void open(InventoryHolder inventory){
        this.viewer.openInventory(inventory.getInventory());
    }


    protected List<String> replaceInLore(List<String> lore, String value, String replace){
        List<String> newLore = new ArrayList<>();
        for(String str : lore){
            newLore.add(str.replace(value, replace));
        }
        return newLore;
    }


    protected String IntegerToString(int value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


    protected String DoubleToString(double value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


    protected void emptyCases(Inventory inventory, Integer slots, Integer start) {
		ItemStack item = new ItemStack(Material.getMaterial(config.getString("emptyCase")), 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
        NamespacedKey key = new NamespacedKey(plugin, "emptyCase");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
		item.setItemMeta(meta);

        for(int i = start; i < slots; i++) {
			inventory.setItem(i, item);
		}
	}

    protected void emptyCases(Inventory inventory, List<Integer> list) {
		ItemStack item = new ItemStack(Material.getMaterial(config.getString("emptyCase")), 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
        NamespacedKey key = new NamespacedKey(plugin, "emptyCase");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
		item.setItemMeta(meta);

        for(int i : list) {
            inventory.setItem(i, item);
        }
	}

    protected String removeFirstColors(String name){
        Pattern pattern = Pattern.compile("§.");
        Matcher matcher = pattern.matcher(name);
        int colors = 0;
        while (matcher.find()) {
            colors++;
        }
        return name.substring(name.length() - (name.length() - colors * pattern.pattern().length()));
    }


    protected boolean isMenuItem(InventoryClickEvent event, String inventoryConfigPath){
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null) return false;
        if(itemStack.getItemMeta() == null) return false;
        if(itemStack.getItemMeta().getDisplayName() == null) return false;
        
        if (itemStack.getItemMeta().getDisplayName().equals(config.getString("inventories." + inventoryConfigPath + ".itemName"))
            && itemStack.getType() == Material.getMaterial(config.getString("inventories." + inventoryConfigPath + ".itemType"))
            && (config.getStringList("inventories." + inventoryConfigPath + ".lore") != null 
                && itemStack.getItemMeta().getLore().equals(config.getStringList("inventories." + inventoryConfigPath + ".lore")))
            && event.getSlot() == config.getInt("inventories." + inventoryConfigPath + ".slot")) {
            return true;
        }

        return false;
    }


    @Override
    public Inventory getInventory() {
        return this.menu.getInventory();
    }
}
