package fr.army.stelyteam.conversation.impl;

import com.mrivanplays.conversations.base.ConversationContext;
import com.mrivanplays.conversations.base.question.Question;
import com.mrivanplays.conversations.spigot.BukkitConversationManager;
import com.mrivanplays.conversations.spigot.BukkitConversationPartner;
import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.chat.Message;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
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
                        Messages.SEND_TEAM_NAME.getMessage()
                ))
                .withQuestion(Question.of(
                        TEAM_DISPLAY_NAME_IDENTIFIER,
                        Messages.SEND_TEAM_PREFIX.getMessage()
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
            respondent.sendMessage(Messages.PREFIX.getMessage() + Messages.ALREADY_IN_TEAM.getMessage());
            return;
        }

        try {
            tPlayer.withdraw(Config.priceCreateTeam);
        } catch (IllegalStateException e) {
            respondent.sendMessage(Messages.PREFIX.getMessage() + Messages.NOT_ENOUGH_MONEY.getMessage());
            return;
        }

        final Team team = new Team(UUID.randomUUID());
        team.getName().set(name);
        team.getDisplayName().set(displayName);
        storageManager.saveTeam(team);
    }
}
