package fr.army.stelyteam.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.army.stelyteam.StelyTeamPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class SubCommand {
    protected StelyTeamPlugin plugin;

    public SubCommand(StelyTeamPlugin plugin){
        this.plugin = plugin;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    @NotNull
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    public abstract boolean isOpCommand();
}
