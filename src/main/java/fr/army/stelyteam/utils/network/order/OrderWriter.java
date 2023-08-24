package fr.army.stelyteam.utils.network.order;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;

public class OrderWriter {
    
    public byte @NotNull [] write(@NotNull Order order) throws IOException {
        final ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        final DataOutputStream outDataStream = new DataOutputStream(outByteStream);
        outDataStream.writeUTF(order.sourceServerName());
        outDataStream.write(order.data());
        return outByteStream.toByteArray();
    }
}
