package nl.enjarai.doabarrelroll.net;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.ServerEvents;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.*;

public class ServerNetworking {
    public static final ServerConfigHolder<ConfigUpdateAckS2CPacket> CONFIG_HOLDER = new ServerConfigHolder<>(
            FMLPaths.CONFIGDIR.get().resolve(DoABarrelRoll.MODID + "-server.json"),
            ModConfigServer.CODEC, ConfigUpdateAckS2CPacket::new, ServerEvents::updateServerConfig
    );
    public static final HandshakeServer<ConfigSyncS2CPacket> HANDSHAKE_SERVER = new HandshakeServer<>(
            ConfigSyncS2CPacket::new, CONFIG_HOLDER, player -> !ModConfigServer.canModify(player));

    public static void init(IEventBus modBus) {
        CONFIG_HOLDER.setHandshakeServer(HANDSHAKE_SERVER);

        modBus.addListener(RegisterPayloadHandlersEvent.class, ServerNetworking::registerPayloads);
        // The initial handshake is sent in the CommandManagerMixin.

        ServerEvents.SERVER_CONFIG_UPDATE.register((server, config) -> {
            for (var player : server.getPlayerManager().getPlayerList()) {
                sendHandshake(player);
            }
        });

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayerEntity player) {
                HANDSHAKE_SERVER.playerDisconnected(player.networkHandler);
            }
        });
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> HANDSHAKE_SERVER.tick(event.getServer()));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // Optional, so vanilla clients can still join modded servers (and the other way around).
        // Whether the client has the mod is negotiated by our own handshake, see HandshakeServer.
        var registrar = event.registrar("1").optional();

        registrar.playToServer(ConfigResponseC2SPacket.PACKET_ID, ConfigResponseC2SPacket.PACKET_CODEC, ServerNetworking::onConfigResponse);
        registrar.playToServer(ConfigUpdateC2SPacket.PACKET_ID, ConfigUpdateC2SPacket.PACKET_CODEC, ServerNetworking::onConfigUpdate);
        registrar.playToServer(RollSyncC2SPacket.PACKET_ID, RollSyncC2SPacket.PACKET_CODEC, ServerNetworking::onRollSync);

        // Client handlers are only ever invoked on the physical client, so referencing ClientNetworking here is safe.
        registrar.playToClient(ConfigSyncS2CPacket.PACKET_ID, ConfigSyncS2CPacket.PACKET_CODEC, (payload, context) -> ClientNetworking.onConfigSync(payload, context));
        registrar.playToClient(ConfigUpdateAckS2CPacket.PACKET_ID, ConfigUpdateAckS2CPacket.PACKET_CODEC, (payload, context) -> ClientNetworking.onConfigUpdateAck(payload));
        registrar.playToClient(RollSyncS2CPacket.PACKET_ID, RollSyncS2CPacket.PACKET_CODEC, (payload, context) -> ClientNetworking.onRollSync(payload));
    }

    private static boolean isAccepted(ServerPlayerEntity player) {
        return HANDSHAKE_SERVER.getHandshakeState(player).state == HandshakeServer.HandshakeState.ACCEPTED;
    }

    private static void onConfigResponse(ConfigResponseC2SPacket payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayerEntity player)) return;

        var reply = HANDSHAKE_SERVER.clientReplied(player.networkHandler, payload);
        if (reply == HandshakeServer.HandshakeState.RESEND) {
            // Resending can happen when the client has a different protocol version than expected.
            sendHandshake(player);
        }
    }

    private static void onRollSync(RollSyncC2SPacket payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayerEntity player) || !isAccepted(player)) return;

        var rollPlayer = (RollEntity) player;
        var isRolling = payload.rolling();
        var roll = payload.roll();

        rollPlayer.doABarrelRoll$setRolling(isRolling);
        rollPlayer.doABarrelRoll$setRoll(isRolling ? MathHelper.wrapDegrees(roll) : 0);
    }

    private static void onConfigUpdate(ConfigUpdateC2SPacket payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayerEntity player) || !isAccepted(player)) return;

        context.reply(CONFIG_HOLDER.clientSendsUpdate(player, payload));
    }

    private static boolean canReceive(ServerPlayerEntity player, CustomPayload payload) {
        return player.networkHandler.hasChannel(payload.getId());
    }

    public static void sendHandshake(ServerPlayerEntity player) {
        var payload = HANDSHAKE_SERVER.initiateConfigSync(player.networkHandler);
        // Vanilla clients haven't negotiated our channel, NeoForge refuses to send to them.
        // We still mark the config as sent so the forceInstalled timeout can kick them.
        if (canReceive(player, payload)) {
            PacketDistributor.sendToPlayer(player, payload);
        }
        HANDSHAKE_SERVER.configSentToClient(player.networkHandler);
    }

    public static void sendRollUpdates(Entity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) return;

        var rollEntity = (RollEntity) entity;
        var isRolling = rollEntity.doABarrelRoll$isRolling();
        var roll = rollEntity.doABarrelRoll$getRoll();

        var payload = new RollSyncS2CPacket(entity.getId(), isRolling, roll);

        world.getChunkManager().chunkLoadingManager.getPlayersWatchingChunk(entity.getChunkPos(), false).stream()
                .filter(player -> player != entity)
                .filter(ServerNetworking::isAccepted)
                .forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }
}
