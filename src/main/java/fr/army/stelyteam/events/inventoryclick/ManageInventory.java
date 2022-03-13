package fr.army.stelyteam.events.inventoryclick;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvAddMember;
import fr.army.stelyteam.conversations.ConvEditTeamID;
import fr.army.stelyteam.conversations.ConvEditTeamPrefix;
import fr.army.stelyteam.conversations.ConvRemoveMember;
import fr.army.stelyteam.utils.InventoryGenerator;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;


public class ManageInventory {
    private InventoryClickEvent event;

    public ManageInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String inventoryName = event.getView().getTitle();

        // Fermeture ou retour en arrière de l'inventaire
        if (StelyTeamPlugin.sqlManager.isOwner(player.getName()) || StelyTeamPlugin.sqlManager.isAdmin(player.getName())){
            if (itemName.equals(StelyTeamPlugin.config.getString("manage.close.itemName")) && inventoryName.equals(StelyTeamPlugin.config.getString("inventoriesName.manage"))){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }

        // Liaisin des items avec leur fonction
        if (itemName.equals(StelyTeamPlugin.config.getString("manage.addMember.itemName"))){
            player.closeInventory();
            StelyTeamPlugin.teamsJoinTeam.add(StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName()));
            getNameInput(player, new ConvAddMember());

        }else if (itemName.equals(StelyTeamPlugin.config.getString("manage.removeMember.itemName"))){
            player.closeInventory();
            StelyTeamPlugin.teamsKickTeam.add(StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName()));
            getNameInput(player, new ConvRemoveMember());

        }else if (itemName.equals(StelyTeamPlugin.config.getString("manage.setTeamHome.itemName"))){
            player.closeInventory();
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName());
            String worldName = player.getWorld().getName();
            Double x = player.getLocation().getX();
            Double y = player.getLocation().getY();
            Double z = player.getLocation().getZ();
            Double yaw = (double) player.getLocation().getYaw();

            if (StelyTeamPlugin.sqliteManager.isSet(teamID)){
                StelyTeamPlugin.sqliteManager.updateHome(teamID, worldName, x, y, z, yaw);
                player.sendMessage("Le home a été redéfini");

            }else{
                StelyTeamPlugin.sqliteManager.addHome(teamID, worldName, x, y, z, yaw);
                player.sendMessage("Le home a été créé");
            }

        }else if (itemName.equals(StelyTeamPlugin.config.getString("manage.removeTeamHome.itemName"))){
            player.closeInventory();
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName());

            if (StelyTeamPlugin.sqliteManager.isSet(teamID)){
                StelyTeamPlugin.sqliteManager.removeHome(teamID);
                player.sendMessage("Le home a été supprimé");
            }else{
                player.sendMessage("Le home n'a pas été défini");
            }
            
        }else if (itemName.equals(StelyTeamPlugin.config.getString("manage.teamBank.itemName"))){
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(player.getName());

            if (!StelyTeamPlugin.sqlManager.hasUnlockedTeamBank(teamID)){
                player.closeInventory();
                StelyTeamPlugin.playersBuyTeamBank.add(player.getName());
                Inventory inventory = InventoryGenerator.createConfirmInventory();
                player.openInventory(inventory);
            }
        }else if (itemName.equals(StelyTeamPlugin.config.getString("manage.updateMembers.itemName"))){
            Inventory inventory = InventoryGenerator.createUpgradeMembersInventory(player.getName());
            player.openInventory(inventory);
        }else if (itemName.equals(StelyTeamPlugin.config.getString("manage.editName.itemName"))){
            player.closeInventory();
            getNameInput(player, new ConvEditTeamID());
        }else if (itemName.equals(StelyTeamPlugin.config.getString("manage.editPrefix.itemName"))){
            player.closeInventory();
            getNameInput(player, new ConvEditTeamPrefix());
        }
    }


    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(StelyTeamPlugin.instance);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}
