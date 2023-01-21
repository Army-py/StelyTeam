package fr.army.stelyteam.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.army.stelyteam.StelyTeamPlugin;

public abstract class SubCommand {
    protected StelyTeamPlugin plugin;

    public SubCommand(StelyTeamPlugin plugin){
        this.plugin = plugin;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    public abstract boolean isOpCommand();
}
