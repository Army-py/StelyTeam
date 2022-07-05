package fr.army.stelyteam.commands;

import org.bukkit.command.CommandSender;

import fr.army.stelyteam.StelyTeamPlugin;

public abstract class SubCommand {
    protected StelyTeamPlugin plugin;

    public SubCommand(StelyTeamPlugin plugin){
        this.plugin = plugin;
    }

    public abstract boolean execute(CommandSender sender, String[] args);
}
