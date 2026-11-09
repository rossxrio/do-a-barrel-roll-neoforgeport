package nl.enjarai.doabarrelroll.render;

import java.util.function.BiConsumer;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public class RenderHelper {
    /**
     * Colour-inverting fill used for the HUD widgets, like the vanilla crosshair.
     * NeoForge keeps {@code RenderPipelines.register} private, so this is registered through
     * {@link RegisterRenderPipelinesEvent} instead (see {@link #registerPipelines}).
     */
    public static final RenderPipeline INVERTED = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(DoABarrelRoll.id("pipeline/inverted_fill"))
            .withColorTargetState(new ColorTargetState(BlendFunction.INVERT))
            .build();

    public static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(INVERTED);
    }

    public static BiConsumer<Integer, Integer> blankPixel(GuiGraphicsExtractor drawContext) {
        return (x, y) -> {
            drawContext.fill(INVERTED, x, y, x + 1, y + 1, 0xffffffff);
        };
    }
}
