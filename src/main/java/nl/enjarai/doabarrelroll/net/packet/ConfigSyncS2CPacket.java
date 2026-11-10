package nl.enjarai.doabarrelroll.net.packet;

import net.minecraft.network.PacketByteBuf;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfigServer;

public record ConfigSyncS2CPacket(int protocolVersion, LimitedModConfigServer applicableConfig, boolean isLimited, ModConfigServer fullConfig) {
    public void write(PacketByteBuf buf) {
        buf.writeInt(protocolVersion);
        LimitedModConfigServer.write(buf, applicableConfig);
        buf.writeBoolean(isLimited);
        fullConfig.write(buf);
    }

    public static ConfigSyncS2CPacket read(PacketByteBuf buf) {
        return new ConfigSyncS2CPacket(
                buf.readInt(),
                LimitedModConfigServer.read(buf),
                buf.readBoolean(),
                ModConfigServer.read(buf)
        );
    }
}
