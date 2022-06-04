package fr.army.stelyteam.storage.network.packet;

import fr.army.stelyteam.storage.TeamField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DatabasePacket implements Packet {

    private String[] dataBases;

    public DatabasePacket() {
    }

    public DatabasePacket(String[] dataBases) {
        this.dataBases = dataBases;
    }


    @Override
    public PacketType getType() {
        return PacketType.DATABASE;
    }

    @Override
    public void encode(DataOutputStream output) throws IOException {
        for (String dataBase : dataBases) {
            output.writeUTF(dataBase);
        }
    }

    @Override
    public void decode(DataInputStream input) throws IOException {
        final String[] dataBases = new String[TeamField.values().length];
        for (int index = 0; index < dataBases.length; index++) {
            dataBases[index] = input.readUTF();
        }
        this.dataBases = dataBases;
    }
}
