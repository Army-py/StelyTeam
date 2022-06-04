package fr.army.stelyteam.storage.network.packet;

import fr.army.stelyteam.storage.network.packet.lifecycle.CheckStoragePacket;
import fr.army.stelyteam.storage.network.packet.lifecycle.ValidStoragePacket;
import fr.army.stelyteam.storage.network.packet.work.DeletePacket;
import fr.army.stelyteam.storage.network.packet.work.ModifyPacket;
import fr.army.stelyteam.storage.network.packet.work.PlayerPacket;

import java.util.function.Supplier;

public enum PacketType {

    DELETE(DeletePacket::new),
    MODIFY(ModifyPacket::new),
    PLAYER(PlayerPacket::new),
    CHECK_STORAGE(CheckStoragePacket::new),
    VALID_STORAGE(ValidStoragePacket::new);

    private final Supplier<Packet> packetCreator;

    PacketType(Supplier<Packet> packetCreator) {
        this.packetCreator = packetCreator;
    }

    public Packet createPacket() {
        return packetCreator.get();
    }

}
