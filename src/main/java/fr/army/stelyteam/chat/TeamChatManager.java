package fr.army.stelyteam.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TeamChatManager {

    private final Lock lock;
    private TeamChatTask teamChatTask;

    public TeamChatManager() {
        lock = new ReentrantLock();
        teamChatTask = null;
    }

    public void sendMessage(@NotNull Player player, @NotNull String message) {
        try {
            lock.lock();
            final boolean isNew = teamChatTask == null || teamChatTask.isFinished();
            if (isNew) {
                teamChatTask = new TeamChatTask(lock);
            }
            teamChatTask.subscribe(player, message);
            if(isNew) {
                new Thread(teamChatTask, "StelyTeam-Chat").start();
            }
        } finally {
            lock.unlock();
        }
    }

}
