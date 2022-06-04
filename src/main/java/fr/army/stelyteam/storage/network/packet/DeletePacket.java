package fr.army.stelyteam.storage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class DeletePacket implements Packet {

    private UUID uuid;

    public DeletePacket() {
    }

    public DeletePacket(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public PacketType getType() {
        return PacketType.DELETE;
    }

    @Override
    public void encode(DataOutputStream output) throws IOException {
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public void decode(DataInputStream input) throws IOException {
        uuid = new UUID(input.readLong(), input.readLong());
    }

}
