package fr.army.stelyteam.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.team.Team;

public class TeamChatFormatHandler {

    @NotNull
    public String handle(@NotNull Player player, @NotNull Team team, @NotNull String format, @NotNull String message) {
        return format
                .replace("{DISPLAYNAME}", player.getDisplayName())
                .replace("{USERNAME}", player.getName())
                .replace("{MESSAGE}", message)
                .replace("{TEAM_PREFIX}", team.getTeamPrefix());
    }

}
