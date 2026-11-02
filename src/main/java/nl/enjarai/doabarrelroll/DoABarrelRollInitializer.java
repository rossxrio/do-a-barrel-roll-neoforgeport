package nl.enjarai.doabarrelroll;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollInitializer {
    public DoABarrelRollInitializer(IEventBus modBus) {
        DoABarrelRoll.init(modBus);
    }
}
