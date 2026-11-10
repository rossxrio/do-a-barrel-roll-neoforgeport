package nl.enjarai.doabarrelroll.net.packet;

import net.minecraft.network.PacketByteBuf;

public record ConfigResponseC2SPacket(int protocolVersion, boolean success) {
    public void write(PacketByteBuf buf) {
        buf.writeInt(protocolVersion);
        buf.writeBoolean(success);
    }

    public static ConfigResponseC2SPacket read(PacketByteBuf buf) {
        return new ConfigResponseC2SPacket(buf.readInt(), buf.readBoolean());
    }
}
