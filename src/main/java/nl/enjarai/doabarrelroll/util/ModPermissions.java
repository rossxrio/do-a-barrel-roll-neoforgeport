package nl.enjarai.doabarrelroll.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * NeoForge permission nodes for the server-side checks. Permission handlers (e.g. LuckPerms) can grant
 * {@code do_a_barrel_roll.ignore_config} / {@code do_a_barrel_roll.configure}; without one, the node
 * falls back to the vanilla operator level passed by the caller.
 */
public class ModPermissions {
    public static final PermissionDynamicContextKey<Integer> DEFAULT_PERMISSION_LEVEL_CONTEXT = new PermissionDynamicContextKey<>(
            Integer.class, "default_permission_level", Objects::toString);

    public static final PermissionNode<Boolean> IGNORE_CONFIG_NODE = new PermissionNode<>(
            DoABarrelRoll.MODID, "ignore_config", PermissionTypes.BOOLEAN,
            ModPermissions::defaultResolve, DEFAULT_PERMISSION_LEVEL_CONTEXT);
    public static final PermissionNode<Boolean> CONFIGURE_NODE = new PermissionNode<>(
            DoABarrelRoll.MODID, "configure", PermissionTypes.BOOLEAN,
            ModPermissions::defaultResolve, DEFAULT_PERMISSION_LEVEL_CONTEXT);

    public static final List<PermissionNode<Boolean>> NODES = List.of(IGNORE_CONFIG_NODE, CONFIGURE_NODE);

    public static boolean resolve(ServerPlayer player, String permission, int defaultPermissionLevel) {
        for (var node : NODES) {
            if (node.getNodeName().equals(permission)) {
                return PermissionAPI.getPermission(player, node, DEFAULT_PERMISSION_LEVEL_CONTEXT.createContext(defaultPermissionLevel));
            }
        }
        return hasPermissionLevel(player, defaultPermissionLevel);
    }

    public static boolean hasPermissionLevel(ServerPlayer player, int level) {
        return player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
    }

    private static boolean defaultResolve(@Nullable ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        if (player != null) {
            for (var key : context) {
                if (key.getDynamic() == DEFAULT_PERMISSION_LEVEL_CONTEXT) {
                    return hasPermissionLevel(player, (int) key.getValue());
                }
            }
        }
        return false;
    }
}
