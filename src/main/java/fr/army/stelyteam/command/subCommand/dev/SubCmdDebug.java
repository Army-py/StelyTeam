package fr.army.stelyteam.command.subCommand.dev;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public boolean isOpCommand() {
        return true;
    }
}
