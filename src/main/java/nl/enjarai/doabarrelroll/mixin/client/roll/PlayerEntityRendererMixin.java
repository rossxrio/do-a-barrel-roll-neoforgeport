package nl.enjarai.doabarrelroll.mixin.client.roll;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import nl.enjarai.doabarrelroll.ModMath;
import nl.enjarai.doabarrelroll.api.RollRenderState;
import nl.enjarai.doabarrelroll.math.MagicNumbers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @ModifyArg(
            method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotate(Lcom/mojang/math/Axis;F)V",
                    ordinal = 0
            ),
            index = 1
    )
    private float doABarrelRoll$modifyRoll(float original, @Local(argsOnly = true) AvatarRenderState state) {
        var rollState = (RollRenderState) state;

        if (rollState.doABarrelRoll$isRolling()) {
            var roll = rollState.doABarrelRoll$getRoll();
            return original + (float) (roll * MagicNumbers.TORAD);
        }

        return original;
    }
}
