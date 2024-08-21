package fr.army.stelyteam.command.subCommand.help;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class SubCmdAdmin extends SubCommand {
    private YamlConfiguration messages;

    public SubCmdAdmin(StelyTeamPlugin plugin) {
        super(plugin);
        this.messages = plugin.getMessages();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        List<String> commandsOp = messages.getStringList("commands.stelyteam_admin.output");

        player.sendMessage(String.join("\n", commandsOp));

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
