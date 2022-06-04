package fr.army.stelyteam.storage.network.packet.lifecycle;

import fr.army.stelyteam.storage.TeamField;
import fr.army.stelyteam.storage.network.packet.Packet;
import fr.army.stelyteam.storage.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CheckStoragePacket implements Packet {

    private String server;
    private TeamField teamField;
    private String dataBase;

    public CheckStoragePacket() {
    }

    public CheckStoragePacket(String server, TeamField teamField, String dataBase) {
        this.server = server;
        this.teamField = teamField;
        this.dataBase = dataBase;
    }

    @Override
    public PacketType getType() {
        return PacketType.CHECK_STORAGE;
    }

    @Override
    public void encode(DataOutputStream output) throws IOException {
        output.writeUTF(server);
        output.writeByte(teamField.ordinal());
        output.writeUTF(dataBase);
    }

    @Override
    public void decode(DataInputStream input) throws IOException {
        final String server = input.readUTF();
        final TeamField teamField = TeamField.values()[input.readByte()];
        final String dataBase = input.readUTF();
        this.server = server;
        this.teamField = teamField;
        this.dataBase = dataBase;
    }
}
