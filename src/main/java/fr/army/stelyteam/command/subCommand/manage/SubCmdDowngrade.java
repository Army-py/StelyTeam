package fr.army.stelyteam.command.subCommand.manage;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.jetbrains.annotations.NotNull;

public class SubCmdDowngrade extends SubCommand {
    private MessageManager messageManager;


    public SubCmdDowngrade(StelyTeamPlugin plugin) {
        super(plugin);
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            player.sendMessage(messageManager.getMessage("commands.stelyteam_downgrade.usage"));
        }else{
            String teamName = String.join("", args);
            Team team = Team.init(teamName);
            if (team != null){
                Integer teamUpgrades = team.getImprovLvlMembers();
                if (teamUpgrades == 0){
                    player.sendMessage(messageManager.getMessage("commands.stelyteam_downgrade.min_level"));
                }else{
                    player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_downgrade.output", teamName));
                    team.decrementImprovLvlMembers();
                }
            }else{
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }


    @Override
    public @NotNull List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }


    @Override
    public boolean isOpCommand() {
        return true;
    }
}
