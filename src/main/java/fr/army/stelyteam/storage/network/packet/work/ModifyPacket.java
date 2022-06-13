package fr.army.stelyteam.storage.network.packet.work;

import fr.army.stelyteam.storage.TeamField;
import fr.army.stelyteam.storage.network.packet.Packet;
import fr.army.stelyteam.storage.network.packet.PacketType;

import java.io.*;
import java.util.UUID;

public class ModifyPacket implements Packet {

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
        output.writeLong(teamId.getMostSignificantBits());
        output.writeLong(teamId.getLeastSignificantBits());
        output.writeByte(field.ordinal());
        final ObjectOutputStream objectStream = new ObjectOutputStream(output);
        objectStream.writeObject(value);
    }

    @Override
    public void decode(DataInputStream input) throws IOException, ClassNotFoundException {
        final UUID teamId = new UUID(input.readLong(), input.readLong());
        final TeamField field = TeamField.values()[input.readByte()];
        final ObjectInputStream objectStream = new ObjectInputStream(input);
        final Object value = objectStream.readObject();
        // Modify instance field at the end to avoid partial decoding
        this.teamId = teamId;
        this.field = field;
        this.value = value;
    }

}
