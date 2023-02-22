package fr.army.stelyteam.events.inventories;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;


public class CreateTeamInventory extends TeamMenu {


    public CreateTeamInventory(Player viewer){
        super(viewer);
    }


    public Inventory createInventory() {
        Integer slots = config.getInt("inventoriesSlots.createTeam");
		Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.createTeam"));
		
        emptyCases(inventory, slots, 0);
		
        Integer slot = config.getInt("inventories.createTeam.slot");
        Material material = Material.getMaterial(config.getString("inventories.createTeam.itemType"));
        String name = config.getString("inventories.createTeam.itemName");
        List<String> lore = config.getStringList("inventories.createTeam.lore");
        String headTexture = config.getString("inventories.createTeam.headTexture");

        inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
		
		return inventory;
    }


    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(config.getString("inventories.createTeam.itemName"))){
            if (plugin.getEconomyManager().checkMoneyPlayer(player, config.getDouble("prices.createTeam"))){
                // Inventory confirmInventory = inventoryBuilder.createConfirmInventory();
                // Inventory inventory = new ConfirmInventory(plugin).createInventory(null);
                // player.openInventory(inventory);
                new ConfirmInventory(viewer).openMenu();
                plugin.getCacheManager().addTempAction(
                    new TemporaryAction(
                        playerName,
                        TemporaryActionNames.CREATE_TEAM,
                        plugin.getDatabaseManager().getTeamFromPlayerName(playerName)));
            }else{
                // player.sendMessage("Vous n'avez pas assez d'argent");
                player.sendMessage(plugin.getMessageManager().getMessage("common.not_enough_money"));
            }
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
