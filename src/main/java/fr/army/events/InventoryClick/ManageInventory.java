package fr.army.events.InventoryClick;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.events.ConvAddMember;
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
            getNameInput(player);
        }
    }


    public void getNameInput(Player player) {
        ConversationFactory cf = new ConversationFactory(App.instance);
        Conversation conv = cf.withFirstPrompt(new ConvAddMember()).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}
