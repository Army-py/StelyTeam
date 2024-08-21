package fr.army.stelyteam.command.subCommand.utility;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.jetbrains.annotations.NotNull;

public class SubCmdVisual extends SubCommand {

    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;

    public SubCmdVisual(StelyTeamPlugin plugin) {
        super(plugin);
        this.messageManager = plugin.getMessageManager();
        this.colorsBuilder = new ColorsBuilder(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            player.sendMessage(messageManager.getMessage("commands.stelyteam_visual.usage"));
        }else{
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_visual.output", colorsBuilder.replaceColor(String.join(" ", args))));
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public boolean isOpCommand() {
        return false;
    }

}
