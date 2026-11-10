package nl.enjarai.doabarrelroll.net;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.ServerEvents;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.*;

import java.util.function.Supplier;

public class ServerNetworking {
    public static final ServerConfigHolder<ConfigUpdateAckS2CPacket> CONFIG_HOLDER = new ServerConfigHolder<>(
            FMLPaths.CONFIGDIR.get().resolve(DoABarrelRoll.MODID + "-server.json"),
            ModConfigServer.CODEC, ConfigUpdateAckS2CPacket::new, ServerEvents::updateServerConfig
    );
    public static final HandshakeServer<ConfigSyncS2CPacket> HANDSHAKE_SERVER = new HandshakeServer<>(
            ConfigSyncS2CPacket::new, CONFIG_HOLDER, player -> !ModConfigServer.canModify(player));

    private static final String CHANNEL_VERSION = Integer.toString(HandshakeServer.PROTOCOL_VERSION);
    /**
     * Single SimpleChannel carrying all of DABR's packets. Every version string is accepted (including
     * {@link NetworkRegistry#ABSENT} and {@link NetworkRegistry#ACCEPTVANILLA}), so the channel is optional:
     * vanilla clients can still join modded servers and the other way around.
     * Whether the other side actually runs the mod is negotiated by our own handshake, see {@link HandshakeServer}.
     */
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(DoABarrelRoll.id("main"))
            .networkProtocolVersion(() -> CHANNEL_VERSION)
            .clientAcceptedVersions(version -> true)
            .serverAcceptedVersions(version -> true)
            .simpleChannel();

    public static void init() {
        CONFIG_HOLDER.setHandshakeServer(HANDSHAKE_SERVER);

        registerPackets();
        // The initial handshake is sent in the CommandManagerMixin.

        ServerEvents.SERVER_CONFIG_UPDATE.register((server, config) -> {
            for (var player : server.getPlayerManager().getPlayerList()) {
                sendHandshake(player);
            }
        });

        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayerEntity player) {
                HANDSHAKE_SERVER.playerDisconnected(player.networkHandler);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                HANDSHAKE_SERVER.tick(event.getServer());
            }
        });
    }

    private static void registerPackets() {
        int id = 0;

        CHANNEL.messageBuilder(ConfigResponseC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ConfigResponseC2SPacket::write)
                .decoder(ConfigResponseC2SPacket::read)
                .consumerMainThread(ServerNetworking::onConfigResponse)
                .add();
        CHANNEL.messageBuilder(ConfigUpdateC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ConfigUpdateC2SPacket::write)
                .decoder(ConfigUpdateC2SPacket::read)
                .consumerMainThread(ServerNetworking::onConfigUpdate)
                .add();
        CHANNEL.messageBuilder(RollSyncC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RollSyncC2SPacket::write)
                .decoder(RollSyncC2SPacket::read)
                .consumerMainThread(ServerNetworking::onRollSync)
                .add();

        // Client handlers are only ever invoked on the physical client, so referencing ClientNetworking here is safe.
        CHANNEL.messageBuilder(ConfigSyncS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ConfigSyncS2CPacket::write)
                .decoder(ConfigSyncS2CPacket::read)
                .consumerMainThread((payload, context) -> ClientNetworking.onConfigSync(payload, context.get()))
                .add();
        CHANNEL.messageBuilder(ConfigUpdateAckS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ConfigUpdateAckS2CPacket::write)
                .decoder(ConfigUpdateAckS2CPacket::read)
                .consumerMainThread((payload, context) -> ClientNetworking.onConfigUpdateAck(payload))
                .add();
        CHANNEL.messageBuilder(RollSyncS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RollSyncS2CPacket::write)
                .decoder(RollSyncS2CPacket::read)
                .consumerMainThread((payload, context) -> ClientNetworking.onRollSync(payload))
                .add();
    }

    private static boolean isAccepted(ServerPlayerEntity player) {
        return HANDSHAKE_SERVER.getHandshakeState(player).state == HandshakeServer.HandshakeState.ACCEPTED;
    }

    private static void onConfigResponse(ConfigResponseC2SPacket payload, Supplier<NetworkEvent.Context> context) {
        var player = context.get().getSender();
        if (player == null) return;

        var reply = HANDSHAKE_SERVER.clientReplied(player.networkHandler, payload);
        if (reply == HandshakeServer.HandshakeState.RESEND) {
            // Resending can happen when the client has a different protocol version than expected.
            sendHandshake(player);
        }
    }

    private static void onRollSync(RollSyncC2SPacket payload, Supplier<NetworkEvent.Context> context) {
        var player = context.get().getSender();
        if (player == null || !isAccepted(player)) return;

        var rollPlayer = (RollEntity) player;
        var isRolling = payload.rolling();
        var roll = payload.roll();

        rollPlayer.doABarrelRoll$setRolling(isRolling);
        rollPlayer.doABarrelRoll$setRoll(isRolling ? MathHelper.wrapDegrees(roll) : 0);
    }

    private static void onConfigUpdate(ConfigUpdateC2SPacket payload, Supplier<NetworkEvent.Context> context) {
        var player = context.get().getSender();
        if (player == null || !isAccepted(player)) return;

        CHANNEL.reply(CONFIG_HOLDER.clientSendsUpdate(player, payload), context.get());
    }

    private static boolean canReceive(ServerPlayerEntity player) {
        return CHANNEL.isRemotePresent(player.networkHandler.connection);
    }

    private static void sendToPlayer(ServerPlayerEntity player, Object payload) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
    }

    public static void sendHandshake(ServerPlayerEntity player) {
        var payload = HANDSHAKE_SERVER.initiateConfigSync(player.networkHandler);
        // Vanilla clients haven't negotiated our channel, so don't bother sending to them.
        // We still mark the config as sent so the forceInstalled timeout can kick them.
        if (canReceive(player)) {
            sendToPlayer(player, payload);
        }
        HANDSHAKE_SERVER.configSentToClient(player.networkHandler);
    }

    public static void sendRollUpdates(Entity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) return;

        var rollEntity = (RollEntity) entity;
        var isRolling = rollEntity.doABarrelRoll$isRolling();
        var roll = rollEntity.doABarrelRoll$getRoll();

        var payload = new RollSyncS2CPacket(entity.getId(), isRolling, roll);

        world.getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(entity.getChunkPos(), false).stream()
                .filter(player -> player != entity)
                .filter(ServerNetworking::isAccepted)
                .forEach(player -> sendToPlayer(player, payload));
    }
}
