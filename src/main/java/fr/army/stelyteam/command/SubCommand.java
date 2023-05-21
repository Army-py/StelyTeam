package fr.army.stelyteam.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.army.stelyteam.StelyTeamPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class SubCommand {
    protected StelyTeamPlugin plugin;

    public SubCommand(StelyTeamPlugin plugin){
        this.plugin = plugin;
    }

    public abstract boolean execute(@NotNull Player player, @NotNull String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    public abstract boolean isOpCommand();
}
