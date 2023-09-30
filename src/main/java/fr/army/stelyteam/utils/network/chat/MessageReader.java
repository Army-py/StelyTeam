package fr.army.stelyteam.utils.network.chat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.chat.Message;

public class MessageReader {
    
    @NotNull
    public Message read(byte @NotNull [] data) throws IOException {
        final DataInputStream inDataStream = new DataInputStream(new ByteArrayInputStream(data));
        final UUID senderUuid = UUID.fromString(inDataStream.readUTF());
        final String message = inDataStream.readUTF();
        final byte recipientsSize = inDataStream.readByte();
        final Set<UUID> recipients = new HashSet<>();
        for (int i = 0; i < recipientsSize; i++) {
            recipients.add(UUID.fromString(inDataStream.readUTF()));
        }

        // System.out.println("READ : " + storageContent.length);
        return new Message(senderUuid, message, recipients);
    }
}
