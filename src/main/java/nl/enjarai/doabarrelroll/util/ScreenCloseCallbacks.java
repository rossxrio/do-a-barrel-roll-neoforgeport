package nl.enjarai.doabarrelroll.util;

import net.minecraft.client.gui.screen.Screen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * NeoForge stand-in for Fabric's {@code ScreenEvents.remove(screen)}: runs callbacks when a specific screen is closed.
 */
public final class ScreenCloseCallbacks {
    private static final Map<Screen, List<Runnable>> CALLBACKS = new WeakHashMap<>();
    private static boolean listening = false;

    private ScreenCloseCallbacks() {
    }

    public static void register(Screen screen, Runnable callback) {
        if (!listening) {
            listening = true;
            NeoForge.EVENT_BUS.addListener(ScreenEvent.Closing.class, ScreenCloseCallbacks::onClosing);
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
