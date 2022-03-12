package fr.army.events.InventoryClick;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.conversations.ConvAddMember;
import fr.army.conversations.ConvRemoveMember;
import fr.army.utils.InventoryGenerator;


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
        if (App.sqlManager.isOwner(player.getName()) || App.sqlManager.isAdmin(player.getName())){
            if (itemName.equals(App.config.getString("manage.close.itemName")) && inventoryName.equals(App.config.getString("inventoriesName.manage"))){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }

        // Liaisin des items avec leur fonction
        if (itemName.equals(App.config.getString("manage.addMember.itemName"))){
            player.closeInventory();
            App.teamsJoinTeam.add(App.sqlManager.getTeamIDFromOwner(player.getName()));
            getNameInput(player, new ConvAddMember());

        }else if (itemName.equals(App.config.getString("manage.removeMember.itemName"))){
            player.closeInventory();
            App.teamsKickTeam.add(App.sqlManager.getTeamIDFromOwner(player.getName()));
            getNameInput(player, new ConvRemoveMember());

        }else if (itemName.equals(App.config.getString("manage.setTeamHome.itemName"))){
            player.closeInventory();
            String teamID = App.sqlManager.getTeamIDFromOwner(player.getName());
            String worldName = player.getWorld().getName();
            Double x = player.getLocation().getX();
            Double y = player.getLocation().getY();
            Double z = player.getLocation().getZ();
            Double yaw = (double) player.getLocation().getYaw();

            if (App.sqliteManager.isSet(teamID)){
                App.sqliteManager.updateHome(teamID, worldName, x, y, z, yaw);
                player.sendMessage("Le home a été redéfini");

            }else{
                App.sqliteManager.addHome(teamID, worldName, x, y, z, yaw);
                player.sendMessage("Le home a été créé");
            }

        }else if (itemName.equals(App.config.getString("manage.removeTeamHome.itemName"))){
            player.closeInventory();
            String teamID = App.sqlManager.getTeamIDFromOwner(player.getName());

            if (App.sqliteManager.isSet(teamID)){
                App.sqliteManager.removeHome(teamID);
                player.sendMessage("Le home a été supprimé");
            }else{
                player.sendMessage("Le home n'a pas été défini");
            }
            
        }else if (itemName.equals(App.config.getString("manage.teamBank.itemName"))){
            String teamID = App.sqlManager.getTeamIDFromPlayer(player.getName());

            if (!App.sqlManager.hasUnlockedTeamBank(teamID)){
                player.closeInventory();
                App.playersBuyTeamBank.add(player.getName());
                Inventory inventory = InventoryGenerator.createConfirmInventory();
                player.openInventory(inventory);
            }
        }else if (itemName.equals(App.config.getString("manage.updateMembers.itemName"))){
            Inventory inventory = InventoryGenerator.createUpgradeMembersInventory(player.getName());
            player.openInventory(inventory);
        }
    }


    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(App.instance);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}
