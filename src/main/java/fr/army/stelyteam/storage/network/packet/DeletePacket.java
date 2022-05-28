package fr.army.stelyteam.storage.network.packet;

import fr.army.stelyteam.util.BinaryUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class DeletePacket implements Packet {

    private final static byte BYTE_ARRAY_LENGTH = Long.BYTES * 2;

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
        final byte[] array = new byte[BYTE_ARRAY_LENGTH];
        BinaryUtils.toByteArray(uuid, array, 0);
        output.write(array);
    }

    @Override
    public void decode(DataInputStream input) throws IOException {
        final byte[] array = new byte[BYTE_ARRAY_LENGTH];
        input.readFully(array);
        uuid = BinaryUtils.toUUID(array, 0);
    }

}
