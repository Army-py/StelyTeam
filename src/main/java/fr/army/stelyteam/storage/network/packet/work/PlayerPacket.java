package fr.army.stelyteam.storage.network.packet.work;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.storage.network.packet.Packet;
import fr.army.stelyteam.storage.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerPacket implements Packet {

    private UUID playerId, teamId;
    private int rank; // -1 for deletion

    public PlayerPacket() {
    }

    public PlayerPacket(UUID playerId, UUID teamId, int rank) {
        this.playerId = playerId;
        this.teamId = teamId;
        this.rank = rank;
    }

    @Override
    public PacketType getType() {
        return PacketType.PLAYER;
    }

    @Override
    public void encode(DataOutputStream output) throws IOException {
        output.writeLong(playerId.getMostSignificantBits());
        output.writeLong(playerId.getLeastSignificantBits());
        output.writeLong(teamId.getMostSignificantBits());
        output.writeLong(teamId.getLeastSignificantBits());
        output.writeByte(rank);
    }

    @Override
    public void decode(DataInputStream input) throws IOException {
        final UUID playerId = new UUID(input.readLong(), input.readLong());
        final UUID teamId = new UUID(input.readLong(), input.readLong());
        final int rank = input.readByte();
        // Modify instance field at the end to avoid partial decoding
        this.playerId = playerId;
        this.teamId = teamId;
        this.rank = rank;
    }

    @Override
    public void handle(StelyTeamPlugin plugin) {
        plugin.getTeamManager().getTeamCache().handlePlayers(this);
    }

}