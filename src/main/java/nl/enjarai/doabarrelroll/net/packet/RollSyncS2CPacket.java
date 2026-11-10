package nl.enjarai.doabarrelroll.net.packet;

import net.minecraft.network.PacketByteBuf;

public record RollSyncS2CPacket(int entityId, boolean rolling, float roll) {
    public void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(rolling);
        buf.writeFloat(roll);
    }

    public static RollSyncS2CPacket read(PacketByteBuf buf) {
        return new RollSyncS2CPacket(buf.readInt(), buf.readBoolean(), buf.readFloat());
    }
}
