package fr.army.stelyteam.conversation;

import com.mrivanplays.conversations.base.ConversationContext;
import com.mrivanplays.conversations.spigot.BukkitConversationManager;
import com.mrivanplays.conversations.spigot.BukkitConversationPartner;
import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class Conversation {

    protected final BukkitConversationManager conversationManager;
    protected final StorageManager storageManager;
    protected final TeamCache teamCache;

    public Conversation(BukkitConversationManager conversationManager, StelyTeamPlugin plugin){
        this.conversationManager = conversationManager;
        this.storageManager = plugin.getStorageManager();
        this.teamCache = plugin.getTeamCache();
    }

    public abstract void run(@NotNull Player player);

    protected abstract void done(ConversationContext<String, BukkitConversationPartner> context);
}
