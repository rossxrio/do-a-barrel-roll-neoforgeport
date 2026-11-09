package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record RollSyncC2SPacket(boolean rolling, float roll) implements CustomPacketPayload {
    public static final Type<RollSyncC2SPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("roll_sync"));
    public static final StreamCodec<ByteBuf, RollSyncC2SPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RollSyncC2SPacket::rolling,
            ByteBufCodecs.FLOAT, RollSyncC2SPacket::roll,
            RollSyncC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
