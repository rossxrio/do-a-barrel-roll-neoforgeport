package nl.enjarai.doabarrelroll.net.packet;

import net.minecraft.network.PacketByteBuf;

public record RollSyncC2SPacket(boolean rolling, float roll) {
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(rolling);
        buf.writeFloat(roll);
    }

    public static RollSyncC2SPacket read(PacketByteBuf buf) {
        return new RollSyncC2SPacket(buf.readBoolean(), buf.readFloat());
    }
}
