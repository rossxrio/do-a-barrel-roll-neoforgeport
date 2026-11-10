package nl.enjarai.doabarrelroll;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import nl.enjarai.doabarrelroll.net.ServerNetworking;
import nl.enjarai.doabarrelroll.util.ModPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoABarrelRoll {
    public static final String MODID = "do_a_barrel_roll";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final Identifier HANDSHAKE_CHANNEL = id("handshake");
    public static final Identifier SERVER_CONFIG_UPDATE_CHANNEL = id("server_config_update");
    public static final Identifier ROLL_CHANNEL = id("player_roll");

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    public static void init() {
        ServerNetworking.init();
    }

    public static PacketByteBuf createBuf() {
        return new PacketByteBuf(Unpooled.buffer());
    }

    public static boolean checkPermission(ServerPlayNetworkHandler handler, String permission, int operatorLevel) {
        return ModPermissions.resolve(handler.getPlayer(), permission, operatorLevel);
    }
}
