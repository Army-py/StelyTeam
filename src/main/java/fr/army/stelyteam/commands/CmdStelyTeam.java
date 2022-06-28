package fr.army.stelyteam.commands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.ColorsBuilder;
import fr.army.stelyteam.utils.InventoryGenerator;
import fr.army.stelyteam.utils.MessageManager;

public class CmdStelyTeam implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            String playerName = player.getName();
            
            if(StelyTeamPlugin.playersCreateTeam.contains(player.getName())){
                return true;
            }

            if (!StelyTeamPlugin.sqliteManager.isRegistered(player.getName())) {
                StelyTeamPlugin.sqliteManager.registerPlayer(player);
            }
            
            if (args.length == 0){
                Inventory inventory;
                if (!StelyTeamPlugin.sqlManager.isMember(playerName)){
                    inventory = InventoryGenerator.createTeamInventory();
                }else if(StelyTeamPlugin.sqlManager.isOwner(player.getName())){
                    inventory = InventoryGenerator.createAdminInventory();
                }else if (StelyTeamPlugin.sqlManager.getMemberRank(playerName) <= 3){
                    inventory = InventoryGenerator.createAdminInventory();
                }else if (StelyTeamPlugin.sqlManager.getMemberRank(playerName) >= 4){
                    inventory = InventoryGenerator.createMemberInventory(playerName);
                }else inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }else{
                if (args[0].equals("home")){
                    String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(player.getName());
                    if (!StelyTeamPlugin.sqliteManager.isSet(teamID)){
                        // player.sendMessage("Le home de team n'est pas défini");
                        player.sendMessage(MessageManager.getMessage("commands.stelyteam_home.not_set"));
                    }else{
                        World world = Bukkit.getWorld(StelyTeamPlugin.sqliteManager.getWorld(teamID));
                        double x = StelyTeamPlugin.sqliteManager.getX(teamID);
                        double y = StelyTeamPlugin.sqliteManager.getY(teamID);
                        double z = StelyTeamPlugin.sqliteManager.getZ(teamID);
                        float yaw = (float) StelyTeamPlugin.sqliteManager.getYaw(teamID);
                        Location location = new Location(world, x, y, z, yaw, 0);
                        // player.sendMessage("Téléportation au home");
                        player.sendMessage(MessageManager.getMessage("commands.stelyteam_home.teleport"));
                        player.teleport(location);
                    }
                }else if (args[0].equals("visual")){
                    args[0] = "";
                    if (args.length == 1){
                        // player.sendMessage("Utilisation : /stelyteam visual <&3 texte>");
                        player.sendMessage(MessageManager.getMessage("commands.stelyteam_visual.usage"));
                    }else{
                        // player.sendMessage("Ton texte :" + new ColorsBuilder().replaceColor(String.join(" ", args)));
                        player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_visual.output", new ColorsBuilder().replaceColor(String.join(" ", args))));
                    }
                }else if (args[0].equals("info")){
                    args[0] = "";
                    if (args.length == 1){
                        // player.sendMessage("Utilisation : /stelyteam info <nom de team>");
                        player.sendMessage(MessageManager.getMessage("commands.stelyteam_info.usage"));
                    }else{
                        String teamID = String.join("", args);
                        if (StelyTeamPlugin.sqlManager.teamIdExist(teamID)){
                            String yesMessage = StelyTeamPlugin.messages.getString("commands.stelyteam_info.true");
                            String noMessage = StelyTeamPlugin.messages.getString("commands.stelyteam_info.false");

                            String teamPrefix = StelyTeamPlugin.sqlManager.getTeamPrefix(teamID);
                            String teamOwner = StelyTeamPlugin.sqlManager.getTeamOwner(teamID);
                            String creationDate = StelyTeamPlugin.sqlManager.getCreationDate(teamID);
                            Integer teamMembersLelvel = StelyTeamPlugin.sqlManager.getTeamLevel(teamID);
                            Integer teamMembers = StelyTeamPlugin.sqlManager.getMembers(teamID).size();
                            Integer maxMembers = StelyTeamPlugin.config.getInt("teamMaxMembers");
                            String hasUnlockBank = (StelyTeamPlugin.sqlManager.hasUnlockedTeamBank(teamID) ? yesMessage : noMessage);
                            List<String> members = StelyTeamPlugin.sqlManager.getMembers(teamID);
                            List<String> lore = StelyTeamPlugin.messages.getStringList("commands.stelyteam_info.output");

                            lore = replaceInLore(lore, "%NAME%", teamID);
                            lore = replaceInLore(lore, "%PREFIX%", new ColorsBuilder().replaceColor(teamPrefix));
                            lore = replaceInLore(lore, "%OWNER%", teamOwner);
                            lore = replaceInLore(lore, "%DATE%", creationDate);
                            lore = replaceInLore(lore, "%UNLOCK_BANK%", hasUnlockBank);
                            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
                            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
                            lore = replaceInLore(lore, "%MEMBERS%", String.join(", ", members));

                            player.sendMessage(String.join("\n", lore));
                        }else{
                            // player.sendMessage("Cette team n'existe pas");
                            player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                        }
                    }
                }else if (sender.isOp()){
                    if (args[0].equals("delete")){
                        args[0] = "";
                        if (args.length == 1){
                            // player.sendMessage("Utilisation : /stelyteam delete <nom de team>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_delete.usage"));
                        }else{
                            String teamID = String.join("", args);
                            if (StelyTeamPlugin.sqlManager.teamIdExist(teamID)){
                                StelyTeamPlugin.sqlManager.removeTeam(teamID);
                                StelyTeamPlugin.sqliteManager.removeHome(teamID);
                                // player.sendMessage("Team supprimée");
                                player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_delete.output", teamID));
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("money")){
                        args[0] = "";
                        if (args.length == 1){
                            // player.sendMessage("Utilisation : /stelyteam money <nom de team>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_money.usage"));
                        }else{
                            String teamID = String.join("", args);
                            if (StelyTeamPlugin.sqlManager.teamIdExist(teamID)){
                                // player.sendMessage(StelyTeamPlugin.sqlManager.getTeamMoney(teamID).toString());
                                player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_money.output", StelyTeamPlugin.sqlManager.getTeamMoney(teamID).toString()));
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("upgrade")){
                        args[0] = "";
                        if (args.length == 1){
                            // player.sendMessage("Utilisation : /stelyteam upgrade <nom de team>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_upgrade.usage"));
                        }else{
                            String teamID = String.join("", args);
                            if (StelyTeamPlugin.sqlManager.teamIdExist(teamID)){
                                Integer maxUpgrades = StelyTeamPlugin.config.getConfigurationSection("inventories.upgradeTotalMembers").getValues(false).size() - 1;
                                Integer teamUpgrades = StelyTeamPlugin.sqlManager.getTeamLevel(teamID);
                                if (maxUpgrades == teamUpgrades){
                                    // player.sendMessage("Cette team est déjà au niveau maximum");
                                    player.sendMessage(MessageManager.getMessage("commands.stelyteam_upgrade.max_level"));
                                }else{
                                    StelyTeamPlugin.sqlManager.incrementTeamLevel(teamID);
                                    // player.sendMessage("Nombre de membres augmenté");
                                    player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_upgrade.output", teamID));
                                }
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("downgrade")){
                        args[0] = "";
                        if (args.length == 1){
                            // player.sendMessage("Utilisation : /stelyteam downgrade <nom de team>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_downgrade.usage"));
                        }else{
                            String teamID = String.join("", args);
                            if (StelyTeamPlugin.sqlManager.teamIdExist(teamID)){
                                Integer teamUpgrades = StelyTeamPlugin.sqlManager.getTeamLevel(teamID);
                                if (teamUpgrades == 0){
                                    // player.sendMessage("Cette team est déjà au niveau minimum");
                                    player.sendMessage(MessageManager.getMessage("commands.stelyteam_downgrade.min_level"));
                                }else{
                                    // player.sendMessage("Nombre de membres diminué");
                                    player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_downgrade.output", teamID));
                                    StelyTeamPlugin.sqlManager.decrementTeamLevel(teamID);
                                }
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("editname")){
                        args[0] = "";
                        if (args.length < 3){
                            // player.sendMessage("Utilisation : /stelyteam editname <nom de team> <nouveau nom>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_editname.usage"));
                        }else{
                            if (StelyTeamPlugin.sqlManager.teamIdExist(args[1])){
                                if (StelyTeamPlugin.sqlManager.teamIdExist(args[2])){
                                    // player.sendMessage("Ce nom de team existe déjà");
                                    player.sendMessage(MessageManager.getMessage("commands.common.,name_already_exist"));
                                }else if (args[2].contains(" ")){
                                    // player.sendMessage("Le nom de team ne doit pas contenir d'espace");
                                    player.sendMessage(MessageManager.getMessage("commands.common.name_cannot_contain_space"));
                                }else{
                                    StelyTeamPlugin.sqlManager.updateTeamID(args[1], args[2]);
                                    // player.sendMessage("Nom de team modifié en " + args[2]);
                                    player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_editname.output", args[2]));
                                }
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("editprefix")){
                        args[0] = "";
                        System.out.println(args.length);
                        if (args.length < 3){
                            // player.sendMessage("Utilisation : /stelyteam editprefix <nom de team> <nouveau prefix>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_editprefix.usage"));
                        }else{
                            if (StelyTeamPlugin.sqlManager.teamIdExist(args[1])){
                                StelyTeamPlugin.sqlManager.updateTeamPrefix(args[1], args[2]);
                                // player.sendMessage("Préfixe de team modifié en " + new ColorsBuilder().replaceColor(args[2]));
                                player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_editprefix.output", new ColorsBuilder().replaceColor(args[2])));
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("changeowner")){
                        args[0] = "";
                        if (args.length < 3){
                            // player.sendMessage("Utilisation : /stelyteam changeowner <nom de team> <membre>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_changeowner.usage"));
                        }else{
                            if (StelyTeamPlugin.sqlManager.teamIdExist(args[1])){
                                if (StelyTeamPlugin.sqlManager.isMemberInTeam(args[2], args[1])){
                                    Integer memberRank = StelyTeamPlugin.sqlManager.getMemberRank(args[2]);
                                    if (memberRank != 0){
                                        String owner = StelyTeamPlugin.sqlManager.getTeamOwner(args[1]);
                                        StelyTeamPlugin.sqlManager.updateTeamOwner(args[1], args[2], owner);
                                        // player.sendMessage("Gérant changé");
                                        player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_changeowner.output", args[2]));
                                    }else{
                                        // player.sendMessage("Ce joueur est déjà le gérant");
                                        player.sendMessage(MessageManager.getMessage("commands.stelyteam_changeowner.already_owner"));
                                    }
                                }else{
                                    // player.sendMessage("Ce joueur n'est pas dans cette team");
                                    player.sendMessage(MessageManager.getMessage("commands.stelyteam_changeowner.not_in_team"));
                                }
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("addmember")){
                        args[0] = "";
                        if (args.length < 3){
                            // player.sendMessage("Utilisation : /stelyteam addmember <nom de team> <membre>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_addmember.usage"));
                        }else{
                            if (StelyTeamPlugin.sqlManager.teamIdExist(args[1])){
                                if (StelyTeamPlugin.sqlManager.isMember(args[2])){
                                    // player.sendMessage("Ce joueur est déjà dans une team");
                                    player.sendMessage(MessageManager.getMessage("common.player_already_in_team"));
                                }else{
                                    StelyTeamPlugin.sqlManager.insertMember(args[2], args[1]);
                                    // player.sendMessage("Joueur ajouté dans la team");
                                    player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_addmember.output", args[2]));
                                }
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else if (args[0].equals("removemember")){
                        args[0] = "";
                        if (args.length < 3){
                            // player.sendMessage("Utilisation : /stelyteam removemember <nom de team> <membre>");
                            player.sendMessage(MessageManager.getMessage("commands.stelyteam_removemember.usage"));
                        }else{
                            if (StelyTeamPlugin.sqlManager.teamIdExist(args[1])){
                                if (StelyTeamPlugin.sqlManager.isMemberInTeam(args[2], args[1])){
                                    StelyTeamPlugin.sqlManager.removeMember(args[2], args[1]);
                                    // player.sendMessage("Joueur retiré de la team");
                                    player.sendMessage(MessageManager.getReplaceMessage("commands.stelyteam_removemember.output", args[2]));
                                }else{
                                    // player.sendMessage("Ce joueur n'est pas dans cette team");
                                    player.sendMessage(MessageManager.getMessage("commands.stelyteam_removemember.not_in_team"));
                                }
                            }else{
                                // player.sendMessage("Cette team n'existe pas");
                                player.sendMessage(MessageManager.getMessage("common.team_not_exist"));
                            }
                        }
                    }else player.sendMessage(MessageManager.getMessage("common.invalid_command"));
                }else player.sendMessage(MessageManager.getMessage("common.invalid_command"));
            }
        }

    return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subCommands = new ArrayList<>();
        List<String> subCommandsOp = new ArrayList<>();
        subCommands.add("home");
        subCommands.add("visual");
        subCommands.add("info");

        if (sender.isOp()){
            subCommandsOp.add("delete");
            subCommandsOp.add("money");
            subCommandsOp.add("upgrade");
            subCommandsOp.add("downgrade");
            subCommandsOp.add("editname");
            subCommandsOp.add("editprefix");
            subCommandsOp.add("changeowner");
            subCommandsOp.add("addmember");
            subCommandsOp.add("removemember");
        }

        if (args.length == 1){
            List<String> result = new ArrayList<>();
            for (String subcommand : subCommands) {
                if (subcommand.toLowerCase().toLowerCase().startsWith(args[0])){
                    result.add(subcommand);
                }
            }
            for (String subcommand : subCommandsOp) {
                if (subcommand.toLowerCase().toLowerCase().startsWith(args[0])){
                    result.add(subcommand);
                }
            }
            return result;
        }else if (args.length == 2){
            if (args[0].equals("info")){
                List<String> result = new ArrayList<>();
                for (String teamID : StelyTeamPlugin.sqlManager.getTeamsIds()) {
                    if (teamID.toLowerCase().startsWith(args[1].toLowerCase())){
                        result.add(teamID);
                    }
                }
                return result;
            }else if (sender.isOp()){
                if (subCommandsOp.contains(args[0])){
                    List<String> result = new ArrayList<>();
                    for (String teamID : StelyTeamPlugin.sqlManager.getTeamsIds()) {
                        if (teamID.toLowerCase().startsWith(args[1].toLowerCase())){
                            result.add(teamID);
                        }
                    }
                    return result;
                }
            }
        }else if (sender.isOp() && args.length == 3){
            if (args[0].equals("changeowner")){
                List<String> result = new ArrayList<>();
                for (String member : StelyTeamPlugin.sqlManager.getMembers(args[1])) {
                    Integer memberRank = StelyTeamPlugin.sqlManager.getMemberRank(member);
                    if (memberRank > 0 && member.toLowerCase().startsWith(args[2].toLowerCase())){
                        result.add(member);
                    }
                }
                return result;
            }else if (args[0].equals("removemember")){
                List<String> result = new ArrayList<>();
                for (String member : StelyTeamPlugin.sqlManager.getMembers(args[1])) {
                    Integer memberRank = StelyTeamPlugin.sqlManager.getMemberRank(member);
                    if (memberRank != 0 && member.toLowerCase().startsWith(args[2].toLowerCase())){
                        result.add(member);
                    }
                }
                return result;
            }
        }

        return null;
    }


    private static List<String> replaceInLore(List<String> lore, String value, String replace){
        List<String> newLore = new ArrayList<>();
        for(String str : lore){
            newLore.add(str.replace(value, replace));
        }
        return newLore;
    }

    
    private static String IntegerToString(Integer value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}
