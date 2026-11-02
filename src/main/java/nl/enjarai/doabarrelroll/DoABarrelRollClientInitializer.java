package nl.enjarai.doabarrelroll;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import nl.enjarai.doabarrelroll.config.ModConfigScreen;

@Mod(value = DoABarrelRoll.MODID, dist = Dist.CLIENT)
public class DoABarrelRollClientInitializer {
    public DoABarrelRollClientInitializer(IEventBus modBus, ModContainer container) {
        DoABarrelRollClient.init();

        modBus.addListener(RegisterKeyMappingsEvent.class, event -> ModKeybindings.ALL.forEach(event::register));

        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (mod, parent) -> ModConfigScreen.create(parent)
        );
    }
}
