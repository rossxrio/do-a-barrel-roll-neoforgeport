package nl.enjarai.doabarrelroll;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import nl.enjarai.doabarrelroll.config.ModConfigScreen;
import nl.enjarai.doabarrelroll.util.StarFoxUtil;

/**
 * Client-only setup. Only ever referenced from {@link DoABarrelRollInitializer} when running on the physical client.
 */
public class DoABarrelRollClientInitializer {
    public static void init(IEventBus modBus) {
        DoABarrelRollClient.init();

        modBus.addListener((RegisterKeyMappingsEvent event) -> ModKeybindings.ALL.forEach(event::register));

        // Forge replaces the vanilla InGameHud#render with its own overlay list, so the easter egg overlay
        // is registered as a Forge GUI overlay instead of being injected into InGameHud#render.
        // It goes below everything else, matching where upstream draws it in the vanilla HUD.
        modBus.addListener((RegisterGuiOverlaysEvent event) -> event.registerBelowAll("peppy",
                (gui, context, partialTick, screenWidth, screenHeight) ->
                        StarFoxUtil.renderPeppy(context, partialTick, screenWidth, screenHeight)));

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ModConfigScreen.create(parent))
        );
    }
}
