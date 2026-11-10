package nl.enjarai.doabarrelroll.util;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Forge stand-in for Fabric's {@code ScreenEvents.remove(screen)}: runs callbacks when a specific screen is closed.
 */
public final class ScreenCloseCallbacks {
    private static final Map<Screen, List<Runnable>> CALLBACKS = new WeakHashMap<>();
    private static boolean listening = false;

    private ScreenCloseCallbacks() {
    }

    public static void register(Screen screen, Runnable callback) {
        if (!listening) {
            listening = true;
            MinecraftForge.EVENT_BUS.addListener(ScreenCloseCallbacks::onClosing);
        }
        CALLBACKS.computeIfAbsent(screen, s -> new ArrayList<>()).add(callback);
    }

    private static void onClosing(ScreenEvent.Closing event) {
        var callbacks = CALLBACKS.remove(event.getScreen());
        if (callbacks != null) {
            callbacks.forEach(Runnable::run);
        }
    }
}
