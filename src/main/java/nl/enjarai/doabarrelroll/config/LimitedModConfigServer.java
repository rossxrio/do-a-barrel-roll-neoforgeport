package nl.enjarai.doabarrelroll.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;

public interface LimitedModConfigServer {
    LimitedModConfigServer OPERATOR = new Impl(true, false);

    static Codec<LimitedModConfigServer> getCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("allowThrusting", ModConfigServer.DEFAULT.allowThrusting()).forGetter(LimitedModConfigServer::allowThrusting),
                Codec.BOOL.optionalFieldOf("forceEnabled", ModConfigServer.DEFAULT.forceEnabled()).forGetter(LimitedModConfigServer::forceEnabled)
        ).apply(instance, Impl::new));
    }

    static void write(PacketByteBuf buf, LimitedModConfigServer config) {
        buf.writeBoolean(config.allowThrusting());
        buf.writeBoolean(config.forceEnabled());
    }

    static LimitedModConfigServer read(PacketByteBuf buf) {
        return new Impl(buf.readBoolean(), buf.readBoolean());
    }

    boolean allowThrusting();

    boolean forceEnabled();

    record Impl(boolean allowThrusting, boolean forceEnabled) implements LimitedModConfigServer {
    }
}
