package fr.army.stelyteam.external.bungeechatconnect.handler;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.army.stelyteam.team.Team;

public class RecipientsHandler {
    
    public void handle(UUID senderId, Set<Player> recipients, Team team) {
        recipients.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(senderId) || team.isTeamMember(senderId)) {
                recipients.add(p);
            }
        }
    }
}
