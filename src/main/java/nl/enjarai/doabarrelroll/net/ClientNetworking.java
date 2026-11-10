package nl.enjarai.doabarrelroll.net;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.ClientEvents;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.*;

public class ClientNetworking {
    public static final HandshakeClient<ConfigResponseC2SPacket> HANDSHAKE_CLIENT = new HandshakeClient<>(
            ConfigResponseC2SPacket::new,
            ClientEvents::updateServerConfig
    );
    public static final ServerConfigUpdateClient<ConfigUpdateC2SPacket> CONFIG_UPDATE_CLIENT = new ServerConfigUpdateClient<>(
            ConfigUpdateC2SPacket::new
    );

    public static void init() {
        // Packet handlers are registered in ServerNetworking#registerPackets and delegate to the methods below.
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> HANDSHAKE_CLIENT.reset());
    }

    static void onConfigSync(ConfigSyncS2CPacket payload, NetworkEvent.Context context) {
        ServerNetworking.CHANNEL.reply(HANDSHAKE_CLIENT.handleConfigSync(payload), context);
    }

    static void onRollSync(RollSyncS2CPacket payload) {
        if (!HANDSHAKE_CLIENT.hasConnected()) return;

        var client = MinecraftClient.getInstance();
        if (client.world == null) {
            return;
        }

        var entity = client.world.getEntityById(payload.entityId());
        if (entity == null) {
            return;
        }
        var rollEntity = (RollEntity) entity;

        rollEntity.doABarrelRoll$setRolling(payload.rolling());
        rollEntity.doABarrelRoll$setRoll(MathHelper.wrapDegrees(payload.roll()));
    }

    static void onConfigUpdateAck(ConfigUpdateAckS2CPacket payload) {
        if (!HANDSHAKE_CLIENT.hasConnected()) return;

        CONFIG_UPDATE_CLIENT.updateAcknowledged(payload);
    }

    private static void send(Object payload) {
        var handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler != null && ServerNetworking.CHANNEL.isRemotePresent(handler.getConnection())) {
            ServerNetworking.CHANNEL.sendToServer(payload);
        }
    }

    public static void sendRollUpdate(RollEntity entity) {
        if (HANDSHAKE_CLIENT.hasConnected()) {
            boolean rolling = entity.doABarrelRoll$isRolling();
            float roll = entity.doABarrelRoll$getRoll();

            send(new RollSyncC2SPacket(rolling, roll));
        }
    }

    public static void sendConfigUpdatePacket(ModConfigServer config) {
        send(CONFIG_UPDATE_CLIENT.prepUpdatePacket(config));
    }
}
