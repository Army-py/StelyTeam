package fr.army.stelyteam.conversation.impl;

import com.mrivanplays.conversations.base.ConversationContext;
import com.mrivanplays.conversations.base.question.Question;
import com.mrivanplays.conversations.spigot.BukkitConversationManager;
import com.mrivanplays.conversations.spigot.BukkitConversationPartner;
import fr.army.stelyteam.conversation.Conversation;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CreationTeamConv extends Conversation {

    private final String TEAM_NAME_IDENTIFIER = "teamName";
    private final String TEAM_DISPLAY_NAME_IDENTIFIER = "teamDisplayName";

    public CreationTeamConv(BukkitConversationManager conversationManager) {
        super(conversationManager);
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
                        "§aMaintenant envoi le préfixe que tu souhaites dans le tchat. §7§o(avec couleur)" // TODO remplacer par la nouvelle config
                ))
                .whenDone(this::done)
                .build()
                .start();
    }

    @Override
    protected void done(ConversationContext<String, BukkitConversationPartner> context) {
        final Player respondent = context.getConversationPartner().getPlayer();
        final String name = context.getInput(TEAM_NAME_IDENTIFIER);
        final String displayName = context.getInput(TEAM_DISPLAY_NAME_IDENTIFIER);

        // TODO remove player money

        final Team team = new Team(UUID.randomUUID());
        team.getOwner().set(new Member(respondent.getUniqueId()));
        team.save();
    }


}
