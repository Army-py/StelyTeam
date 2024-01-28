package fr.army.stelyteam.conversation;

import com.mrivanplays.conversations.base.ConversationContext;
import com.mrivanplays.conversations.spigot.BukkitConversationManager;
import com.mrivanplays.conversations.spigot.BukkitConversationPartner;
import fr.army.stelyteam.cache.StorageManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class Conversation {

    protected final BukkitConversationManager conversationManager;
    protected final StorageManager storageManager;

    public Conversation(BukkitConversationManager conversationManager, StorageManager storageManager){
        this.conversationManager = conversationManager;
        this.storageManager = storageManager;
    }

    public abstract void run(@NotNull Player player);

    protected abstract void done(ConversationContext<String, BukkitConversationPartner> context);
}
