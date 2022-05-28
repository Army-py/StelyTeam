package fr.army.stelyteam.storage.network.packet;

import java.util.function.Supplier;

public enum PacketType {

    DELETE(DeletePacket::new),
    MODIFY(ModifyPacket::new),
    PLAYER(PlayerPacket::new);

    private final Supplier<Packet> packetCreator;

    PacketType(Supplier<Packet> packetCreator) {
        this.packetCreator = packetCreator;
    }

    public Packet createPacket() {
        return packetCreator.get();
    }

}
