package fr.army.stelyteam.command.subCommand.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.config.message.Placeholders;
import fr.army.stelyteam.team.Team;

public class SubCmdUpgrade extends SubCommand {

    public SubCmdUpgrade(StelyTeamPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            player.sendMessage(Messages.COMMAND_STELYTEAM_UPGRADE_USAGE.getMessage());
        }else{
            String teamName = String.join("", args);
            Team team = Team.init(teamName);
            if (team != null){
                Integer maxUpgrades = Config.pricesUpgradeMembersLimit.length - 1;
                Integer teamUpgrades = team.getImprovLvlMembers();
                if (maxUpgrades == teamUpgrades){
                    player.sendMessage(Messages.COMMAND_STELYTEAM_UPGRADE_MAX_LEVEL.getMessage());
                }else{
                    team.incrementImprovLvlMembers();
                    Map<Placeholders, String> replaces = new HashMap<>();
                    replaces.put(Placeholders.TEAM_NAME, team.getTeamName());
                    replaces.put(Placeholders.TEAM_PREFIX, team.getTeamPrefix());
                    player.sendMessage(Messages.COMMAND_STELYTEAM_UPGRADE_OUTPUT.getMessage(replaces));
                }
            }else{
                player.sendMessage(Messages.TEAM_DOES_NOT_EXIST.getMessage());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return true;
    }

}
