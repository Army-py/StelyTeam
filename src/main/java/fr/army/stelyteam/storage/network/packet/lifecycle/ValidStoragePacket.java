package fr.army.stelyteam.storage.network.packet.lifecycle;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.storage.TeamField;
import fr.army.stelyteam.storage.network.packet.Packet;
import fr.army.stelyteam.storage.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ValidStoragePacket implements Packet {

    private String server;
    private TeamField teamField;

    public ValidStoragePacket() {
    }

    public ValidStoragePacket(String server, TeamField teamField) {
        this.server = server;
        this.teamField = teamField;
    }

    public String getServer() {
        return server;
    }

    public TeamField getTeamField() {
        return teamField;
    }

    @Override
    public PacketType getType() {
        return PacketType.VALID_STORAGE;
    }

    @Override
    public void encode(DataOutputStream output) throws IOException {
        output.writeUTF(server);
        output.writeByte(teamField.ordinal());
    }

    @Override
    public void decode(DataInputStream input) throws IOException {
        final String server = input.readUTF();
        final TeamField teamField = TeamField.values()[input.readByte()];
        this.server = server;
        this.teamField = teamField;
    }

    @Override
    public void handle(StelyTeamPlugin plugin) {
        plugin.getNetworkManager().getStoragePairManager().handleValid(this);
    }

}
