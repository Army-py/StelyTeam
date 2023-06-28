package fr.army.stelyteam.menu.impl;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.FixedMenu;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;


public class CreateTeamMenu extends FixedMenu {


    public CreateTeamMenu(Player viewer, TeamMenu previousMenu) {
        super(
            viewer,
            Menus.CREATE_TEAM_MENU.getName(),
            Menus.CREATE_TEAM_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory() {
		Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
		
        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.createTeam").getKeys(false)){
            Integer slot = config.getInt("inventories.createTeam."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.createTeam."+buttonName+".itemType"));
            String displayName = config.getString("inventories.createTeam."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.createTeam."+buttonName+".lore");
            String headTexture = config.getString("inventories.createTeam."+buttonName+".headTexture");
            inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
        }
		
		return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();

        if (Buttons.CREATE_TEAM_BUTTON.isClickedButton(clickEvent)){
            if (plugin.getEconomyManager().checkMoneyPlayer(player, config.getDouble("prices.createTeam"))){
                new ConfirmMenu(viewer, this).openMenu();
                plugin.getCacheManager().addTempAction(
                    new TemporaryAction(
                        playerName,
                        TemporaryActionNames.CREATE_TEAM,
                        Team.initFromPlayerName(playerName)));
            }else{
                player.sendMessage(plugin.getMessageManager().getMessage("common.not_enough_money"));
            }
        }else if (Buttons.TEAM_LIST_MENU_BUTTON.isClickedButton(clickEvent)){
            new TeamListMenu(viewer, this).openMenu(0);
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
