package nl.enjarai.doabarrelroll.net.packet;

import net.minecraft.network.PacketByteBuf;

public record ConfigUpdateAckS2CPacket(int protocolVersion, boolean success) {
    public void write(PacketByteBuf buf) {
        buf.writeInt(protocolVersion);
        buf.writeBoolean(success);
    }

    public static ConfigUpdateAckS2CPacket read(PacketByteBuf buf) {
        return new ConfigUpdateAckS2CPacket(buf.readInt(), buf.readBoolean());
    }
}
