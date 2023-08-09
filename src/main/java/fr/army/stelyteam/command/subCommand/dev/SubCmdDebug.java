package fr.army.stelyteam.command.subCommand.dev;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;

public class SubCmdDebug extends SubCommand {

    public SubCmdDebug(StelyTeamPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        
        plugin.toggleDebug();

        sender.sendMessage("Debug mode: " + plugin.isDebug());
        
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
