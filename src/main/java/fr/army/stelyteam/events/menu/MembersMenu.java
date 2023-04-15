package fr.army.stelyteam.events.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.utils.Member;
import fr.army.stelyteam.utils.Menus;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;


public class MembersMenu extends TeamMenu {

    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();

    public MembersMenu(Player viewer) {
        super(
            viewer,
            Menus.TEAM_MEMBERS_MENU.getName(),
            Menus.TEAM_MEMBERS_MENU.getSlots()
        );
    }

    public MembersMenu(Player viewer, String menuName){
        super(
            viewer,
            menuName,
            Menus.TEAM_MEMBERS_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);
        Integer headSlot = 0;
        for(Member member : team.getTeamMembers()){
            String memberName = member.getMemberName();
            UUID playerUUID = plugin.getSQLiteManager().getUUID(member.getMemberName());
            String itemName;
            List<String> lore = new ArrayList<>();
            OfflinePlayer offlinePlayer;

            if (playerUUID == null) offlinePlayer = Bukkit.getOfflinePlayer(memberName);
            else offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);

            String memberRank = plugin.getRankFromId(team.getMemberRank(memberName));
            String rankColor = config.getString("ranks." + memberRank + ".color");
            itemName = rankColor + memberName;
            
            lore.add(config.getString("prefixRankLore") + rankColor + config.getString("ranks." + memberRank + ".name"));
            inventory.setItem(headSlot, ItemBuilder.getPlayerHead(offlinePlayer, itemName, lore));
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.teamMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.teamMembers."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamMembers."+str+".itemType"));
            String name = config.getString("inventories.teamMembers."+str+".itemName");
            String headTexture = config.getString("inventories.teamMembers."+str+".headTexture");
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, Collections.emptyList(), headTexture, false));
        }
        return inventory;
    }


    public void openMenu(Team team) {
        this.open(createInventory(team));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material material = clickEvent.getCurrentItem().getType();
        Team team = Team.init(player);
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
                    new ConfirmMenu(player).openMenu();
                }
            }else if (itemName.equals(config.getString("inventories.teamMembers.close.itemName"))){
                // new EditMembersMenu(player).openMenu(team);
                new EditMembersMenu(player).openMenu(team);
            }
        }else{
            if (itemName.equals(config.getString("inventories.teamMembers.close.itemName"))){
                new MemberMenu(player).openMenu(team);
            }
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {
        Player player = (Player) closeEvent.getPlayer();
        String playerName = player.getName();

        if (closeEvent.getView().getTitle().equals(config.getString("inventoriesName.removeMembers"))){
            if (cacheManager.playerHasAction(playerName)){
                cacheManager.removePlayerActionName(playerName, TemporaryActionNames.CLICK_REMOVE_MEMBER);;
            }
        }
    }
}
