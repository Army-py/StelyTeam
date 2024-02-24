package fr.army.stelyteam.conversation.impl;

import com.mrivanplays.conversations.base.ConversationContext;
import com.mrivanplays.conversations.base.question.Question;
import com.mrivanplays.conversations.spigot.BukkitConversationManager;
import com.mrivanplays.conversations.spigot.BukkitConversationPartner;
import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.conversation.Conversation;
import fr.army.stelyteam.entity.impl.MemberEntity;
import fr.army.stelyteam.entity.impl.TeamEntity;
import fr.army.stelyteam.team.TPlayer;
import fr.army.stelyteam.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public class CreationTeamConv extends Conversation {

    private final String TEAM_NAME_IDENTIFIER = "teamName";
    private final String TEAM_DISPLAY_NAME_IDENTIFIER = "teamDisplayName";

    public CreationTeamConv(BukkitConversationManager conversationManager, StelyTeamPlugin plugin) {
        super(conversationManager, plugin);
    }

    @Override
    public void run(@NotNull Player player) {
        conversationManager.newConversationBuilder(player)
                .withQuestion(Question.of(
                        TEAM_NAME_IDENTIFIER,
                        "§aÉcris le nom de la team dans le tchat. §7§o(sans couleur)" // TODO remplacer par la nouvelle config
                ))
                .withQuestion(Question.of(
                        TEAM_DISPLAY_NAME_IDENTIFIER,
                        "§aMaintenant envoie le préfixe que tu souhaites dans le tchat. §7§o(avec couleur)" // TODO remplacer par la nouvelle config
                ))
                .whenDone(this::done)
                .build()
                .start();
    }

    @Override
    protected void done(ConversationContext<String, BukkitConversationPartner> context) {
        final Player respondent = context.getConversationPartner().getPlayer();
        final TPlayer tPlayer = teamCache.getTPlayer(respondent.getUniqueId());
        final String name = context.getInput(TEAM_NAME_IDENTIFIER);
        final String displayName = context.getInput(TEAM_DISPLAY_NAME_IDENTIFIER);

        if (tPlayer.hasTeam()) {
            respondent.sendMessage("§cTu es déjà dans une team."); // TODO remplacer par la nouvelle config
            return;
        }

        try {
            tPlayer.withdraw(1000); // TODO remplacer par la nouvelle config
        } catch (IllegalStateException e) {
            respondent.sendMessage("§cTu n'as pas assez d'argent pour créer une team."); // TODO remplacer par la nouvelle config
            return;
        }

        final Team team = new Team(UUID.randomUUID());
        team.getName().set(name);
        team.getDisplayName().set(displayName);
        storageManager.saveTeam(team);
    }
}
