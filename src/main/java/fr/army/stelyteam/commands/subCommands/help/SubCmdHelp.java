package fr.army.stelyteam.commands.subCommands.help;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;

public class SubCmdHelp extends SubCommand {
    private YamlConfiguration messages;

    public SubCmdHelp(StelyTeamPlugin plugin){
        super(plugin);
        this.messages = plugin.getMessages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        List<String> commandsPlayer = messages.getStringList("commands.stelyteam_help.output");

        player.sendMessage(String.join("\n", commandsPlayer));
        
        return true;
    }
}
