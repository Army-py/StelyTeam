package fr.army.stelyteam.command.subCommand.info;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class SubCmdInfo extends SubCommand {
    private DatabaseManager sqlManager;


    public SubCmdInfo(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getDatabaseManager();
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";
        if (args.length == 1){
            player.sendMessage(Messages.COMMAND_STELYTEAM_INFO_USAGE.getMessage());
        }else{
            String answer = String.join("", args);
            Team team = Team.initFromPlayerName(answer) == null ? Team.init(answer) : Team.initFromPlayerName(answer);
            if (team != null){
                String yesMessage = Messages.VALUE_TRUE.getMessage();
                String noMessage = Messages.VALUE_FALSE.getMessage();
                Integer maxMembers = Config.teamMaxMembersLimit;

                String teamName = team.getTeamName();
                String teamPrefix = team.getTeamPrefix();
                String teamOwner = team.getTeamOwnerName();
                String teamCreationDate = team.getCreationDate();
                String teamDescription = team.getTeamDescription();
                Integer teamMembersLelvel = team.getImprovLvlMembers();
                String unlockedBank = (team.isUnlockedTeamBank() ? yesMessage : noMessage);
                List<String> teamMembers = team.getMembersName();
                teamMembers.remove(teamOwner);

                String teamMembersStr = teamMembers.isEmpty() ? Messages.NO_TEAM_MEMBERS.getMessage()
                        : String.join(", ", teamMembers);

                List<String> lore = Messages.COMMAND_STELYTEAM_INFO_OUTPUT.getListMessage(
                        IntegerToString(maxMembers + teamMembersLelvel), maxMembers.toString(),
                        teamOwner,
                        teamName, ColorsBuilder.replaceColor(teamPrefix), unlockedBank,
                        IntegerToString(teamMembers.size() + 1), teamMembersStr, teamCreationDate, teamDescription);

                
                // TODO: delete
                // lore = replaceInLore(lore, "%NAME%", teamName);
                // lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(teamPrefix));
                // lore = replaceInLore(lore, "%OWNER%", teamOwner);
                // lore = replaceInLore(lore, "%DATE%", creationDate);
                // lore = replaceInLore(lore, "%UNLOCK_BANK%", hasUnlockBank);
                // lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers.size()+1));
                // lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
                // lore = replaceInLore(lore, "%MEMBERS%", teamMembers.isEmpty() ? messageManager.getMessageWithoutPrefix("common.no_members") : String.join(", ", teamMembers));
                // lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(teamDescription));

                player.sendMessage(String.join("\n", lore));
            }else{
                player.sendMessage(Messages.TEAM_DOES_NOT_EXIST.getMessage());
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2){
            if (args[0].equals("info")){
                List<String> result = new ArrayList<>();
                for (String teamID : sqlManager.getTeamsName()) {
                    if (teamID.toLowerCase().startsWith(args[1].toLowerCase())){
                        result.add(teamID);
                    }
                }
                for (String playerName : sqlManager.getMembers()) {
                    if (playerName.toLowerCase().startsWith(args[1].toLowerCase())){
                        result.add(playerName);
                    }
                }
                return result;
            }
        }
        return null;
    }


    @Override
    public boolean isOpCommand() {
        return false;
    }


    private String IntegerToString(Integer value){
        // TODO: voir pour mettre le local en config
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


}
