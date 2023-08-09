package fr.army.stelyteam.utils.network.message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.team.Storage;

public class OrderReader {
    

    @NotNull
    public Storage read(byte @NotNull [] storageData) throws IOException {
        final DataInputStream inDataStream = new DataInputStream(new ByteArrayInputStream(storageData));
        final UUID teamUuid = UUID.fromString(inDataStream.readUTF());
        final int storageId = inDataStream.readInt();
        final byte[] storageContent = new byte[inDataStream.readShort()];
        final boolean isOpen = inDataStream.readBoolean();
        return new Storage(teamUuid, storageId, null, storageContent, isOpen);
    }
}
