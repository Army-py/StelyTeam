package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvEditTeamID;
import fr.army.stelyteam.conversations.ConvEditTeamPrefix;
import fr.army.stelyteam.events.PlayerChat;
import fr.army.stelyteam.utils.InventoryGenerator;

public class ConfirmInventory {
    private InventoryClickEvent event;

    public ConfirmInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Ouverture des inventaires
        if (StelyTeamPlugin.playersCreateTeam.contains(playerName)){
            player.closeInventory();
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
                player.sendMessage("Envoie un nom de team");
                StelyTeamPlugin.instance.getServer().getPluginManager().registerEvents(new PlayerChat(playerName), StelyTeamPlugin.instance);
            }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.playersCreateTeam.remove(playerName);
                Inventory inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }
        }else if (StelyTeamPlugin.playersJoinTeam.contains(playerName)){
            player.closeInventory();
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
                StelyTeamPlugin.sqlManager.insertMember(playerName, StelyTeamPlugin.teamsJoinTeam.get(StelyTeamPlugin.playersJoinTeam.indexOf(playerName)));
                player.sendMessage("Vous avez rejoint la team " + StelyTeamPlugin.teamsJoinTeam.get(StelyTeamPlugin.playersJoinTeam.indexOf(playerName)));
                StelyTeamPlugin.teamsJoinTeam.remove(StelyTeamPlugin.playersJoinTeam.indexOf(playerName));
                StelyTeamPlugin.playersJoinTeam.remove(playerName);
            }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.teamsJoinTeam.remove(StelyTeamPlugin.playersJoinTeam.indexOf(playerName));
                StelyTeamPlugin.playersJoinTeam.remove(playerName);
            }
        }else if (StelyTeamPlugin.teamsKickTeam.contains(StelyTeamPlugin.sqlManager.getTeamIDFromOwner(playerName))){
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromOwner(playerName);
            String member = StelyTeamPlugin.playersKickTeam.get(StelyTeamPlugin.teamsKickTeam.indexOf(teamID));
            player.closeInventory();
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
                StelyTeamPlugin.sqlManager.removeMember(member, teamID);
                player.sendMessage("Tu as retiré " + member + " de la team");
                StelyTeamPlugin.playersKickTeam.remove(member);
                StelyTeamPlugin.teamsKickTeam.remove(teamID);
            }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.playersKickTeam.remove(member);
                StelyTeamPlugin.teamsKickTeam.remove(teamID);
            }
        }else if (StelyTeamPlugin.playersBuyTeamBank.contains(playerName)){
            player.closeInventory();
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.sqlManager.updateUnlockTeamBank(teamID);
                player.sendMessage("Tu as debloqué le compte de la team");
                StelyTeamPlugin.playersBuyTeamBank.remove(playerName);

                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.playersBuyTeamBank.remove(playerName);

                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }
        }else if (StelyTeamPlugin.playersEditTeamName.contains(playerName)){
            player.closeInventory();
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
                StelyTeamPlugin.playersEditTeamName.remove(playerName);
                getNameInput(player, new ConvEditTeamID());
            }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.playersEditTeamName.remove(playerName);

                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }
        }else if (StelyTeamPlugin.playersEditTeamPrefix.contains(playerName)){
            player.closeInventory();
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
                StelyTeamPlugin.playersEditTeamPrefix.remove(playerName);
                getNameInput(player, new ConvEditTeamPrefix());
            }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.playersEditTeamPrefix.remove(playerName);

                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }
        }else if (StelyTeamPlugin.playersDeleteTeam.contains(playerName)){
            player.closeInventory();
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
                StelyTeamPlugin.playersDeleteTeam.remove(playerName);
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.sqlManager.removeTeam(teamID, playerName);
                player.sendMessage("Tu as supprimé la team");
            }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.playersDeleteTeam.remove(playerName);

                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }
        }
    }


    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(StelyTeamPlugin.instance);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}
