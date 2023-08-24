package fr.army.stelyteam.utils.network.task.chat;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.chat.Message;
import fr.army.stelyteam.chat.TeamChatManager;
import fr.army.stelyteam.utils.network.chat.MessageReader;
import fr.army.stelyteam.utils.network.order.Order;
import fr.army.stelyteam.utils.network.order.OrderReader;

public class ReceiveChatRunnable implements Runnable {

    private final StelyTeamPlugin plugin;
    private final TeamChatManager teamChatManager;
    private final byte[] orderData;

    public ReceiveChatRunnable(StelyTeamPlugin plugin, byte[] orderData) {
        this.plugin = plugin;
        this.teamChatManager = plugin.getTeamChatManager();
        this.orderData = orderData;
    }

    @Override
    public void run() {
        final OrderReader orderReader = new OrderReader();
        final MessageReader messageReader = new MessageReader();

        try {
            final Order order = orderReader.read(orderData);
            final Message message = messageReader.read(order.data());
            if (order.sourceServerName().equals(plugin.getCurrentServerName())) {
                return;
            }

            teamChatManager.sendMessage(message.senderUuid(), message.messageFormat(), message.recipients());
            // System.out.println("Receive chat");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
