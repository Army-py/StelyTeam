package fr.army.stelyteam.events.inventories;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class MembersInventory {

    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private YamlConfiguration config;
    private CacheManager cacheManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;
    private DatabaseManager sqlManager;

    public MembersInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.clickEvent = clickEvent;
        this.config = plugin.getConfig();
        this.cacheManager = plugin.getCacheManager();
        this.messageManager = plugin.getMessageManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
        this.sqlManager = plugin.getDatabaseManager();
    }

    public MembersInventory(InventoryCloseEvent closeEvent, StelyTeamPlugin plugin){
        this.closeEvent = closeEvent;
        this.config = plugin.getConfig();
        this.cacheManager = plugin.getCacheManager();
    }


    public void onInventoryClick(){
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material material = clickEvent.getCurrentItem().getType();
        Team team = sqlManager.getTeamFromPlayerName(playerName);
        String memberName = removeFirstColors(itemName);

        if (clickEvent.getView().getTitle().equals(config.getString("inventoriesName.removeMembers"))){
            if (material.equals(Material.getMaterial("PLAYER_HEAD"))){
                if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CLICK_REMOVE_MEMBER)){
                    if (!team.isTeamMember(playerName)){
                        player.sendRawMessage(messageManager.getMessage("common.player_not_in_your_team"));
                        return;
                    }else if (playerName.equals(memberName)){
                        player.sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_yourself"));
                        return;
                    }else if (team.getMemberRank(memberName) <= team.getMemberRank(playerName)){
                        player.sendRawMessage(messageManager.getMessage("manage_members.remove_member.cant_exclude_higher_rank"));
                        return;
                    }

                    cacheManager.removePlayerAction(playerName);
                    cacheManager.addTempAction(
                        new TemporaryAction(
                                playerName,
                                memberName,
                                TemporaryActionNames.REMOVE_MEMBER,
                                team
                            )
                        );
                    Inventory inventory = inventoryBuilder.createConfirmInventory();
                    player.openInventory(inventory);
                }
            }else if (itemName.equals(config.getString("inventories.teamMembers.close.itemName"))){
                Inventory inventory = inventoryBuilder.createEditMembersInventory(playerName, team);
                player.openInventory(inventory);
            }
        }else{
            if (itemName.equals(config.getString("inventories.teamMembers.close.itemName"))){
                Inventory inventory = inventoryBuilder.createMemberInventory(playerName, team);
                player.openInventory(inventory);
            }
        }
    }


    public void onInventoryClose(){
        Player player = (Player) closeEvent.getPlayer();
        String playerName = player.getName();

        if (closeEvent.getView().getTitle().equals(config.getString("inventoriesName.removeMembers"))){
            if (cacheManager.playerHasAction(playerName)){
                cacheManager.removePlayerActionName(playerName, TemporaryActionNames.CLICK_REMOVE_MEMBER);;
            }
        }
    }


    private String removeFirstColors(String name){
        Pattern pattern = Pattern.compile("§.");
        Matcher matcher = pattern.matcher(name);
        int colors = 0;
        while (matcher.find()) {
            colors++;
        }
        return name.substring(name.length() - (name.length() - colors * pattern.pattern().length()));
    }
}
