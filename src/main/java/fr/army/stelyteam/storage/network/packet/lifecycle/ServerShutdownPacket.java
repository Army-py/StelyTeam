package fr.army.stelyteam.storage.network.packet.lifecycle;

import fr.army.stelyteam.storage.network.packet.Packet;
import fr.army.stelyteam.storage.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerShutdownPacket implements Packet {

    private String server;

    @Override
    public PacketType getType() {
        return PacketType.SERVER_SHUTDOWN;
    }

    public String getServer() {
        return server;
    }

    @Override
    public void encode(DataOutputStream output) throws IOException {
        output.writeUTF(server);
    }

    @Override
    public void decode(DataInputStream input) throws IOException {
        this.server = input.readUTF();
    }
}
