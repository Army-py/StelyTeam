package fr.army.stelyteam.storage.network.packet;

import fr.army.stelyteam.storage.TeamField;
import fr.army.stelyteam.util.BinaryUtils;

import java.io.*;
import java.util.UUID;

public class ModifyPacket implements Packet {

    private final static byte UUID_BYTES = Long.BYTES * 2;
    private final static byte BYTE_ARRAY_LENGTH = UUID_BYTES + 1;

    private UUID teamId;
    private TeamField field;
    private Object value;

    public ModifyPacket() {
    }

    public ModifyPacket(UUID teamId, TeamField field, Object fieldValue) {
        this.teamId = teamId;
        this.field = field;
        this.value = fieldValue;
    }

    @Override
    public PacketType getType() {
        return PacketType.MODIFY;
    }

    @Override
    public void encode(DataOutputStream output) throws IOException {
        final byte[] array = new byte[BYTE_ARRAY_LENGTH];
        BinaryUtils.toByteArray(teamId, array, 0);
        array[UUID_BYTES] = (byte) field.ordinal();
        output.write(array);
        final ObjectOutputStream objectStream = new ObjectOutputStream(output);
        objectStream.writeObject(value);
    }

    @Override
    public void decode(DataInputStream input) throws IOException, ClassNotFoundException {
        final byte[] array = new byte[BYTE_ARRAY_LENGTH];
        input.readFully(array);
        final UUID teamId = BinaryUtils.toUUID(array, 0);
        final TeamField field = TeamField.values()[array[UUID_BYTES]];
        final ObjectInputStream objectStream = new ObjectInputStream(input);
        final Object value = objectStream.readObject();
        // Modify instance field at the end to avoid partial decoding
        this.teamId = teamId;
        this.field = field;
        this.value = value;
    }

}
