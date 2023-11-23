package fr.army.stelyteam.command.subcommand.utility;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubCmdVisual extends SubCommand {

    private final MessageManager messageManager;
    private final ColorsBuilder colorsBuilder;

    public SubCmdVisual(StelyTeamPlugin plugin) {
        super(plugin);
        this.messageManager = plugin.getMessageManager();
        this.colorsBuilder = new ColorsBuilder(plugin);
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_visual.usage"));
            return true;
        }

        args[0] = "";
        final String output = colorsBuilder.replaceColor(String.join(" ", args));
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_visual.output", output));
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
