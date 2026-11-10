package nl.enjarai.doabarrelroll;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Forge 47 (and therefore NeoForge 1.20.1) only allows a single {@code @Mod} class per mod id,
 * so the client half is initialised from here when running on the physical client.
 */
@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollInitializer {
    public DoABarrelRollInitializer() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        DoABarrelRoll.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            DoABarrelRollClientInitializer.init(modBus);
        }
    }
}
