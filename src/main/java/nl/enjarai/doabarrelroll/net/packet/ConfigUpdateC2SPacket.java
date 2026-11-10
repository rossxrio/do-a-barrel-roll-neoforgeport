package nl.enjarai.doabarrelroll.net.packet;

import net.minecraft.network.PacketByteBuf;
import nl.enjarai.doabarrelroll.config.ModConfigServer;

public record ConfigUpdateC2SPacket(int protocolVersion, ModConfigServer config) {
    public void write(PacketByteBuf buf) {
        buf.writeInt(protocolVersion);
        config.write(buf);
    }

    public static ConfigUpdateC2SPacket read(PacketByteBuf buf) {
        return new ConfigUpdateC2SPacket(buf.readInt(), ModConfigServer.read(buf));
    }
}
