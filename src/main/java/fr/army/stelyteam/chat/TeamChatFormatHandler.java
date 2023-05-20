package fr.army.stelyteam.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamChatFormatHandler {

    @NotNull
    public String handle(@NotNull Player player, @NotNull String format, @NotNull String message) {
        return format
                .replace("{USERNAME}", player.getName())
                .replace("{MESSAGE}", message);
    }

}
