package fr.army.stelyteam.chat;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.api.event.PlayerTeamChatEvent;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;

public class TeamChatProcessor {



    private final String format;

    public TeamChatProcessor(String format) {
        this.format = format;
    }

    public void process(@NotNull Player sender, @NotNull String message) {
        final PlayerTeamChatEvent event = new PlayerTeamChatEvent(!Bukkit.isPrimaryThread(), sender, format, message);
        if (event.isCancelled()) {
            return;
        }
        final Team team = Team.getFromCache(sender);
        if (team == null) {
            return;
        }
        final TeamChatFormatHandler formatHandler = new TeamChatFormatHandler();
        final String toSendMessage = formatHandler.handle(sender, team, event.getFormat(), event.getMessage());
        final Collection<Member> recipients = team.getTeamMembers();
        for (Member member : recipients) {
            final Player receiver = member.asPlayer();
            if (receiver == null) {
                continue;
            }
            receiver.sendMessage(toSendMessage);
        }
    }

}
