package fr.army.stelyteam.utils.network.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.team.Storage;

public class OrderWriter {
    
    public byte @NotNull [] write(@NotNull Storage storage) throws IOException {
        final ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        final DataOutputStream outDataStream = new DataOutputStream(outByteStream);
        outDataStream.writeUTF(storage.getTeamUuid().toString());
        outDataStream.writeInt(storage.getStorageId());
        if (storage.getOpenedServerName() == null) {
            outDataStream.writeUTF("null");
        }else{
            outDataStream.writeUTF(storage.getOpenedServerName());
        }
        // System.out.println("WRITE : " + storage.getStorageContent().length);
        outDataStream.write(storage.getStorageContent());
        return outByteStream.toByteArray();
    }
}
