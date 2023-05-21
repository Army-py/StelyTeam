package fr.army.stelyteam.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class TeamChatTask implements Runnable {

    private final Lock lock;
    private final TeamChatProcessor processor;
    private final Queue<Message> messageQueue;
    private boolean finished;

    public TeamChatTask(@NotNull Lock lock, @NotNull TeamChatProcessor processor) {
        this.lock = lock;
        this.processor = processor;
        this.messageQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        Message message;
        while (true) {
            try {
                lock.lock();
                message = messageQueue.poll();
                if (message == null) {
                    finished = true;
                    return;
                }
            } finally {
                lock.unlock();
            }
            processor.process(message.sender(), message.message());
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void subscribe(@NotNull Player player, @NotNull String message) {
        try {
            lock.lock();
            messageQueue.offer(new Message(player, message));
        } finally {
            lock.unlock();
        }
    }

    private record Message(@NotNull Player sender, @NotNull String message) {
    }
}