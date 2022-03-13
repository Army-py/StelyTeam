package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.PlayerChat;
import fr.army.stelyteam.utils.InventoryGenerator;

public class ConfirmInventory {
    private InventoryClickEvent event;

    public ConfirmInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        System.out.println(StelyTeamPlugin.playersCreateTeam);
        System.out.println(StelyTeamPlugin.playersJoinTeam);
        System.out.println(StelyTeamPlugin.teamsJoinTeam);

        // Ouverture des inventaires
        if (StelyTeamPlugin.playersCreateTeam.contains(player.getName())){
            if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.confirm.itemName"))){
                player.closeInventory();
                player.sendMessage("Envoie un nom de team");
                StelyTeamPlugin.instance.getServer().getPluginManager().registerEvents(new PlayerChat(player.getName()), StelyTeamPlugin.instance);
            }else if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.cancel.itemName"))){
                StelyTeamPlugin.playersCreateTeam.remove(player.getName());
                Inventory inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }
        }else if (StelyTeamPlugin.playersJoinTeam.contains(player.getName())){
            if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.confirm.itemName"))){
                player.closeInventory();
                StelyTeamPlugin.sqlManager.insertMember(player.getName(), StelyTeamPlugin.teamsJoinTeam.get(StelyTeamPlugin.playersJoinTeam.indexOf(player.getName())));
                player.sendMessage("Vous avez rejoint la team " + StelyTeamPlugin.teamsJoinTeam.get(StelyTeamPlugin.playersJoinTeam.indexOf(player.getName())));
                StelyTeamPlugin.teamsJoinTeam.remove(StelyTeamPlugin.playersJoinTeam.indexOf(player.getName()));
                StelyTeamPlugin.playersJoinTeam.remove(player.getName());
            }else if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.cancel.itemName"))){
                player.closeInventory();
                StelyTeamPlugin.teamsJoinTeam.remove(StelyTeamPlugin.playersJoinTeam.indexOf(player.getName()));
                StelyTeamPlugin.playersJoinTeam.remove(player.getName());
            }
        }else if (StelyTeamPlugin.teamsKickTeam.contains(StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName()))){
            String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName());
            String member = StelyTeamPlugin.playersKickTeam.get(StelyTeamPlugin.teamsKickTeam.indexOf(teamID));
            if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.confirm.itemName"))){
                player.closeInventory();
                StelyTeamPlugin.sqlManager.removeMember(member, teamID);
                player.sendMessage("Tu as retiré " + member + " de la team");
                StelyTeamPlugin.playersKickTeam.remove(member);
                StelyTeamPlugin.teamsKickTeam.remove(teamID);
            }else if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.cancel.itemName"))){
                player.closeInventory();
                StelyTeamPlugin.playersKickTeam.remove(member);
                StelyTeamPlugin.teamsKickTeam.remove(teamID);
            }
        }else if (StelyTeamPlugin.playersBuyTeamBank.contains(player.getName())){
            if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.confirm.itemName"))){
                player.closeInventory();
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(player.getName());
                StelyTeamPlugin.sqlManager.updateUnlockTeamBank(teamID);
                player.sendMessage("Tu as debloqué le compte de la team");
                StelyTeamPlugin.playersBuyTeamBank.remove(player.getName());

                Inventory inventory = InventoryGenerator.createManageInventory(player.getName());
                player.openInventory(inventory);
            }else if (itemName.equals(StelyTeamPlugin.config.getString("confirmInventory.cancel.itemName"))){
                player.closeInventory();
                StelyTeamPlugin.playersBuyTeamBank.remove(player.getName());

                Inventory inventory = InventoryGenerator.createManageInventory(player.getName());
                player.openInventory(inventory);
            }
        }
    }
}
