package nl.enjarai.doabarrelroll.net;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
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
        // Payload handlers are registered in ServerNetworking#registerPayloads and delegate to the methods below.
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event -> HANDSHAKE_CLIENT.reset());
    }

    static void onConfigSync(ConfigSyncS2CPacket payload, IPayloadContext context) {
        context.reply(HANDSHAKE_CLIENT.handleConfigSync(payload));
    }

    static void onRollSync(RollSyncS2CPacket payload) {
        if (!HANDSHAKE_CLIENT.hasConnected()) return;

        var client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        var entity = client.level.getEntity(payload.entityId());
        if (entity == null) {
            return;
        }
        var rollEntity = (RollEntity) entity;

        rollEntity.doABarrelRoll$setRolling(payload.rolling());
        rollEntity.doABarrelRoll$setRoll(Mth.wrapDegrees(payload.roll()));
    }

    static void onConfigUpdateAck(ConfigUpdateAckS2CPacket payload) {
        if (!HANDSHAKE_CLIENT.hasConnected()) return;

        CONFIG_UPDATE_CLIENT.updateAcknowledged(payload);
    }

    private static void send(CustomPacketPayload payload) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null && connection.hasChannel(payload)) {
            ClientPacketDistributor.sendToServer(payload);
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
