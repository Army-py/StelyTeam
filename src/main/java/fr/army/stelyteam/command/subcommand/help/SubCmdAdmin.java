package fr.army.stelyteam.command.subcommand.help;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubCmdAdmin extends SubCommand {

    private final YamlConfiguration messages;

    public SubCmdAdmin(StelyTeamPlugin plugin) {
        super(plugin);
        this.messages = plugin.getMessages();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        List<String> commandsOp = messages.getStringList("commands.stelyteam_admin.output");

        player.sendMessage(String.join("\n", commandsOp));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return true;
    }

}
