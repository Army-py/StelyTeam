package fr.army.stelyteam.commands.subCommands.info;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.Member;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;

public class SubCmdInfo extends SubCommand {
    private MySQLManager sqlManager;
    private YamlConfiguration config;
    private YamlConfiguration messages;
    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;


    public SubCmdInfo(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messages = plugin.getMessages();
        this.messageManager = plugin.getMessageManager();
        this.colorsBuilder = new ColorsBuilder(plugin);
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";
        if (args.length == 1){
            // player.sendMessage("Utilisation : /stelyteam info <nom de team>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_info.usage"));
        }else{
            String memberName = String.join("", args);
            // String teamID = sqlManager.getTeamNameFromPlayerName(memberName) == null ? String.join("", args) : sqlManager.getTeamNameFromPlayerName(memberName);
            Team team = sqlManager.getTeamFromPlayerName(memberName) == null ? sqlManager.getTeamFromTeamName(String.join("", args)) : sqlManager.getTeamFromPlayerName(memberName);
            if (team != null){
                String yesMessage = messages.getString("commands.stelyteam_info.true");
                String noMessage = messages.getString("commands.stelyteam_info.false");
                Integer maxMembers = config.getInt("teamMaxMembers");

                String teamName = team.getTeamName();
                String teamPrefix = team.getTeamPrefix();
                String teamOwner = team.getTeamOwnerName();
                String creationDate = team.getCreationDate();
                String teamDescription = team.getTeamDescription();
                Integer teamMembersLelvel = team.getImprovLvlMembers();
                String hasUnlockBank = (team.isUnlockedTeamBank() ? yesMessage : noMessage);
                List<Member> teamMembers = team.getTeamMembers();
                List<String> lore = messages.getStringList("commands.stelyteam_info.output");

                lore = replaceInLore(lore, "%NAME%", teamName);
                lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(teamPrefix));
                lore = replaceInLore(lore, "%OWNER%", teamOwner);
                lore = replaceInLore(lore, "%DATE%", creationDate);
                lore = replaceInLore(lore, "%UNLOCK_BANK%", hasUnlockBank);
                lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers.size()));
                lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
                lore = replaceInLore(lore, "%MEMBERS%", String.join(", ",  team.getMembersName()));
                lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(teamDescription));

                player.sendMessage(String.join("\n", lore));
            }else{
                // player.sendMessage("Cette team n'existe pas");
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
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


    private List<String> replaceInLore(List<String> lore, String value, String replace){
        List<String> newLore = new ArrayList<>();
        for(String str : lore){
            newLore.add(str.replace(value, replace));
        }
        return newLore;
    }


    private String IntegerToString(Integer value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


}
