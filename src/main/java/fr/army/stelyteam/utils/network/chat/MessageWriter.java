package fr.army.stelyteam.utils.network.chat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.chat.Message;

public class MessageWriter {
    
    public byte @NotNull [] write(@NotNull Message message) throws IOException {
        final ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        final DataOutputStream outDataStream = new DataOutputStream(outByteStream);
        outDataStream.writeUTF(message.senderUuid().toString());
        outDataStream.writeUTF(message.messageFormat());

        final Set<UUID> recipientsUuids = message.recipients();
        outDataStream.writeByte(recipientsUuids.size());
        for (UUID recipientUuid : recipientsUuids) {
            outDataStream.writeUTF(recipientUuid.toString());
        }
        return outByteStream.toByteArray();
    }
}
