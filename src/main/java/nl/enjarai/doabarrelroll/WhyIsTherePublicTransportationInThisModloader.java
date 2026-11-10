package nl.enjarai.doabarrelroll;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import nl.enjarai.doabarrelroll.util.ModPermissions;

@Mod.EventBusSubscriber(modid = DoABarrelRoll.MODID)
public class WhyIsTherePublicTransportationInThisModloader {
    @SubscribeEvent
    public static void gatherPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(ModPermissions.NODES.toArray(new PermissionNode[0]));
    }
}
