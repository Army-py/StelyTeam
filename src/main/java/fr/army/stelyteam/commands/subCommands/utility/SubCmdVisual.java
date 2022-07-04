package fr.army.stelyteam.commands.subCommands.utility;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.ColorsBuilder;
import fr.army.stelyteam.utils.MessageManager;

public class SubCmdVisual extends SubCommand {

    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;

    public SubCmdVisual(StelyTeamPlugin plugin) {
        super(plugin);
        this.messageManager = plugin.getMessageManager();
        // this.colorsBuilder = plugin.getColorsBuilder();
        this.colorsBuilder = new ColorsBuilder();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            // player.sendMessage("Utilisation : /stelyteam visual <&3 texte>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_visual.usage"));
        }else{
            // player.sendMessage("Ton texte :" + new ColorsBuilder().replaceColor(String.join(" ", args)));
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_visual.output", colorsBuilder.replaceColor(String.join(" ", args))));
        }
        return true;
    }

}
