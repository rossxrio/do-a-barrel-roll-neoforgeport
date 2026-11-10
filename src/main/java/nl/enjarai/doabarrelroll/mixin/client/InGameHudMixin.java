package nl.enjarai.doabarrelroll.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import nl.enjarai.doabarrelroll.EventCallbacksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forge renders the HUD through ForgeGui's overlay list instead of {@code InGameHud#render}, but its
 * crosshair overlay still calls {@code InGameHud#renderCrosshair}, so the flight widgets hook in there.
 * The matrix push/pop lets the momentum widget offset the vanilla crosshair drawn in between.
 * The Star Fox easter egg is registered as a separate Forge overlay in DoABarrelRollClientInitializer.
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "HEAD"
            )
    )
    private void doABarrelRoll$renderCrosshairHead(DrawContext context, CallbackInfo ci) {
        context.getMatrices().push();
        EventCallbacksClient.onRenderCrosshair(context, MinecraftClient.getInstance().getTickDelta(),
                context.getScaledWindowWidth(), context.getScaledWindowHeight());
    }

    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "RETURN"
            )
    )
    private void doABarrelRoll$renderCrosshairReturn(DrawContext context, CallbackInfo ci) {
        context.getMatrices().pop();
    }
}
