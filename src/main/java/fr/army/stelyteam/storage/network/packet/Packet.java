package fr.army.stelyteam.storage.network.packet;

import fr.army.stelyteam.StelyTeamPlugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {

    PacketType getType();

    /**
     * Encode a packet
     *
     * @param output The {@link DataOutputStream} where to write the packet
     */
    void encode(DataOutputStream output) throws IOException;

    /**
     * Decode a packet
     *
     * @param input the {@link DataInputStream} where to read the packet
     */
    void decode(DataInputStream input) throws IOException, ClassNotFoundException;


    /**
     * Handle a packet
     *
     * @param plugin The {@link StelyTeamPlugin} instance to get the handler instance
     */
    void handle(StelyTeamPlugin plugin);

}
