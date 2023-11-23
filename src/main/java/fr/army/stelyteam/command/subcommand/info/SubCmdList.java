package fr.army.stelyteam.command.subcommand.info;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.menu.impl.TeamListMenu;
import fr.army.stelyteam.utils.manager.CacheManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubCmdList extends SubCommand {

    private final CacheManager cacheManager;

    public SubCmdList(StelyTeamPlugin plugin) {
        super(plugin);
        this.cacheManager = plugin.getCacheManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        String playerName = player.getName();

        cacheManager.removePage(cacheManager.getPage(playerName));
        new TeamListMenu(player, null).openMenu(0);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return false;
    }

}
