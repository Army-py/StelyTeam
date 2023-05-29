package fr.army.stelyteam.command.subcommand.info;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.TeamField;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SubCmdMoney extends SubCommand {

    private final TeamManager teamManager;
    //private DatabaseManager sqlManager;
    private final MessageManager messageManager;

    public SubCmdMoney(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        this.teamManager = plugin.getTeamManager();
        //this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_money.usage"));
            return true;
        }
        args[0] = "";
        final String teamName = String.join("", args);
        final Team team = teamManager.getTeam(teamName, TeamField.BANK_BALANCE);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final Double balance = team.getBankAccount().getBalance().get();
        final double balanceValue = balance == null ? 0 : balance;
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_money.output", DoubleToString(balanceValue)));
        return true;
    }

    private String DoubleToString(double value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
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
