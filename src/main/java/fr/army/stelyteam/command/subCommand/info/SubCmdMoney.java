package fr.army.stelyteam.command.subCommand.info;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.jetbrains.annotations.NotNull;

public class SubCmdMoney extends SubCommand {
    private MessageManager messageManager;

    public SubCmdMoney(StelyTeamPlugin plugin) {
        super(plugin);
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            player.sendMessage(messageManager.getMessage("commands.stelyteam_money.usage"));
        }else{
            String teamName = String.join("", args);
            Team team = Team.init(teamName);
            if (team != null){
                player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_money.output", DoubleToString(team.getTeamMoney())));
            }else{
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }


    private String DoubleToString(double value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
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
