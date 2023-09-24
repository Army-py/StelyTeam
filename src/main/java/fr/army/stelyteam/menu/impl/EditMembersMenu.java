package fr.army.stelyteam.menu.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.conversation.ConvAddMember;
import fr.army.stelyteam.conversation.ConvEditOwner;
import fr.army.stelyteam.menu.FixedMenuOLD;
import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilderOLD;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class EditMembersMenu extends FixedMenuOLD {

    private final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    private final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();

    public EditMembersMenu(Player viewer, TeamMenuOLD previousMenu){
        super(
            viewer,
            MenusOLD.EDIT_MEMBERS_MENU.getName(),
            MenusOLD.EDIT_MEMBERS_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(String playerName) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);
        Integer headSlot = 0;
        for(String memberName : team.getMembersName()){
            UUID playerUUID = mySqlManager.getUUID(memberName);
            String itemName;
            List<String> lore = new ArrayList<>();
            // OfflinePlayer member;
            ItemStack item;

            // if (playerUUID == null) member = Bukkit.getOfflinePlayer(memberName);
            // else member = Bukkit.getOfflinePlayer(playerUUID);

            Integer memberRank = team.getMemberRank(memberName);
            String memberRankName = plugin.getRankFromId(memberRank);
            String rankColor = config.getString("ranks." + memberRankName + ".color");
            itemName = rankColor + memberName;
            
            if (!team.isTeamOwner(memberName)){
                lore = config.getStringList("editMembersLores");
                if (plugin.getLastRank() == memberRank) lore.remove(1);
            }

            lore.add(0, config.getString("prefixRankLore") + rankColor + config.getString("ranks." + memberRankName + ".name"));
            
            if (plugin.playerHasPermission(playerName, team, "manageMembers")){ 
                item = ItemBuilderOLD.getPlayerHead(playerUUID, itemName, lore);
            }else{
                item = ItemBuilderOLD.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    "noPermission",
                    itemName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String buttonName : config.getConfigurationSection("inventories.editMembers").getKeys(false)){
            Integer slot = config.getInt("inventories.editMembers."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.editMembers."+buttonName+".itemType"));
            String displayName = config.getString("inventories.editMembers."+buttonName+".itemName");
            String headTexture = config.getString("inventories.editMembers."+buttonName+".headTexture");
            ItemStack item;

            if (plugin.playerHasPermission(playerName, team, buttonName)){ 
                item = ItemBuilderOLD.getItem(
                    material,
                    buttonName,
                    displayName,
                    config.getStringList("inventories.editMembers."+buttonName+".lore"),
                    headTexture,
                    false);
            }else{
                item = ItemBuilderOLD.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    "noPermission",
                    displayName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(slot, item);
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory(viewer.getName()));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        String itemName;
        Material itemType = clickEvent.getCurrentItem().getType();
        List<String> lore = clickEvent.getCurrentItem().getItemMeta().getLore();
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        Team team = Team.initFromPlayerName(playerName);


        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }


        // Fermeture ou retour en arrière de l'inventaire
        itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        if (Buttons.CLOSE_EDIT_MEMBERS_MENU_BUTTON.isClickedButton(clickEvent)){
            // new ManageMenu(player, this).openMenu();
            previousMenu.openMenu();
            return;
        }else if (Buttons.ADD_MEMBER_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvAddMember(plugin));
            return;
        }else if (Buttons.REMOVE_MEMBER_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(
                new TemporaryAction(playerName, TemporaryActionNames.CLICK_REMOVE_MEMBER, team)
            );
            new MembersMenu(player, MenusOLD.REMOVE_MEMBERS_MENU.getName(), this).openMenu();
            return;
        }else if (Buttons.EDIT_OWNER_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvEditOwner(plugin));
            return;
        }

        itemName = removeFirstColors(clickEvent.getCurrentItem().getItemMeta().getDisplayName());
        if (team.isTeamMember(playerName)){
            Integer authorRank = team.getMemberRank(playerName);
            Integer memberRank = team.getMemberRank(itemName);
            Player member = Bukkit.getPlayer(itemName);

            if (clickEvent.getClick().isRightClick()){
                if (memberRank <= authorRank){
                    return;
                }
                if (!team.isTeamOwner(itemName) && memberRank < plugin.getLastRank()){
                    team.getMember(itemName).demoteMember();

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank+1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        member.sendMessage(messageManager.getReplaceMessage("receiver.demote", newRankColor + newRankName));
                    }
                }
            }else if (clickEvent.getClick().isLeftClick()){
                if (memberRank-1 <= authorRank){
                    return;
                }
                if (!mySqlManager.isOwner(itemName) && memberRank != 1){
                    team.getMember(itemName).promoteMember();

                    if (member != null && removeFirstColors(itemName).equals(member.getName())){
                        String newRank = plugin.getRankFromId(memberRank-1);
                        String newRankName = config.getString("ranks." + newRank + ".name");
                        String newRankColor = config.getString("ranks." + newRank + ".color");
                        member.sendMessage(messageManager.getReplaceMessage("receiver.promote", newRankColor + newRankName));
                    }
                }
            }else return;

            new EditMembersMenu(player, previousMenu).openMenu();
            team.refreshTeamMembersInventory(playerName);
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}
