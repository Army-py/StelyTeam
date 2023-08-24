package fr.army.stelyteam.utils.network.order;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;

public class OrderReader {
    

    @NotNull
    public Order read(byte @NotNull [] data) throws IOException {
        final DataInputStream inDataStream = new DataInputStream(new ByteArrayInputStream(data));
        final String sourceServerName = inDataStream.readUTF();
        final byte[] storageData = inDataStream.readAllBytes();
        // System.out.println("READ : " + storageContent.length);
        return new Order(sourceServerName, storageData);
    }
}
