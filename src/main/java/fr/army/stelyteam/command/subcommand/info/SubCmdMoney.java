package fr.army.stelyteam.command.subcommand.info;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamField;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SubCmdMoney extends SubCommand {

    private final StorageManager storageManager;
    //private DatabaseManager sqlManager;
    private final MessageManager messageManager;

    public SubCmdMoney(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        this.storageManager = plugin.getStorageManager();
        //this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        args[0] = "";

        if (args.length == 1) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_money.usage"));
            return true;
        }
        final String teamName = String.join("", args);
        final Team team = storageManager.retreiveTeam(teamName, TeamField.BALANCE);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final Double balance = team.getBankAccount().getBalance().retrieve();
        final String balanceReadableValue = balance == null ? "0" : balance.toString();
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_money.output", balanceReadableValue));
        return true;
    }

    /* TODO Check if we really need this
    private String DoubleToString(double value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }*/

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return true;
    }
}
