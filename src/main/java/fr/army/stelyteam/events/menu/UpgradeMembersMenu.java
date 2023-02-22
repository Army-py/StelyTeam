package fr.army.stelyteam.events.menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;



public class UpgradeMembersMenu extends TeamMenu {

    final MessageManager messageManager = plugin.getMessageManager();

    public UpgradeMembersMenu(Player viewer){
        super(viewer);
    }


    public Inventory createInventory(Team team) {
        Integer slots = config.getInt("inventoriesSlots.upgradeTotalMembers");
        Integer level = team.getImprovLvlMembers();
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.upgradeTotalMembers"));
        Material material;
        String headTexture;

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.upgradeTotalMembers."+str+".slot");
            String name = config.getString("inventories.upgradeTotalMembers."+str+".itemName");
            List<String> lore = config.getStringList("inventories.upgradeTotalMembers."+str+".lore");

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


    public void openMenu(Team team){
        this.open(createInventory(team));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material material = clickEvent.getCurrentItem().getType();

        Team team = plugin.getDatabaseManager().getTeamFromPlayerName(playerName);
        Integer level = team.getImprovLvlMembers();
        
        if (itemName.equals(config.getString("inventories.upgradeTotalMembers.close.itemName"))){
            new ManageMenu(player).openMenu(team);
        }else if (!material.name().equals(config.getString("emptyCase"))){
            for(String str : config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
                String name = config.getString("inventories.upgradeTotalMembers."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    if (plugin.getEconomyManager().checkMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+(level+1)))){
                        plugin.getCacheManager().addTempAction(new TemporaryAction(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS, team));
                        new ConfirmMenu(player).openMenu();
                        return;
                    }else{
                        player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                        return;
                    }
                }else if (itemName.equals(name) && level >= config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    player.sendMessage(messageManager.getMessage("common.already_unlocked"));
                    return;
                }
            }
            player.sendMessage(messageManager.getMessage("manage_team.upgrade_member_amount.must_unlock_previous_level"));
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
