package nl.enjarai.doabarrelroll.mixin.client.roll;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import nl.enjarai.doabarrelroll.api.RollCamera;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.math.MagicNumbers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements RollCamera {
    @Shadow private Entity entity;

    @Unique
    private boolean isRolling;
    @Unique
    private float lastRollBack;
    @Unique
    private float rollBack;
    @Unique
    private float appliedRoll;
    // NeoForge patches a roll field (and setRotation(FFF)) into Camera, so we feed our roll through that.
    @Shadow
    private float roll;

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Camera;eyeHeight:F",
                    ordinal = 0
            )
    )
    private void doABarrelRoll$interpolateRollnt(CallbackInfo ci) {
        if (!((RollEntity) entity).doABarrelRoll$isRolling()) {
            lastRollBack = rollBack;
            rollBack -= rollBack * 0.5f;
        }
    }

    @Inject(
            method = "alignWithEntity",
            at = @At("HEAD")
    )
    private void doABarrelRoll$captureTickDeltaAndUpdate(float tickDelta, CallbackInfo ci, @Share("tickDelta") LocalFloatRef tickDeltaRef) {
        tickDeltaRef.set(tickDelta);
        isRolling = ((RollEntity) entity).doABarrelRoll$isRolling();
        appliedRoll = 0;
    }

    @Inject(
            method = "alignWithEntity",
            at = @At("TAIL")
    )
    private void doABarrelRoll$updateRollBack(float tickDelta, CallbackInfo ci) {
        if (isRolling) {
            // Use the roll we applied rather than this.roll, which the mirrored camera negates.
            rollBack = appliedRoll;
            lastRollBack = appliedRoll;
        }
    }

    /*
     * NeoForge's alignWithEntity calls setRotation(yaw, pitch, roll) with the roll from
     * ViewportEvent.ComputeCameraAngles (ordinal 0). The mirrored third-person call (ordinal 1)
     * already passes -this.roll, so it picks our roll up without a second modification.
     */
    @ModifyArg(
            method = "alignWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FFF)V",
                    ordinal = 0
            ),
            index = 2
    )
    private float doABarrelRoll$addRoll(float original, @Share("tickDelta") LocalFloatRef tickDelta) {
        if (isRolling) {
            appliedRoll = ((RollEntity) entity).doABarrelRoll$getRoll(tickDelta.get());
        } else {
            appliedRoll = Mth.lerp(tickDelta.get(), lastRollBack, rollBack);
        }
        return original + appliedRoll;
    }


    @Override
    public float doABarrelRoll$getRoll() {
        return roll;
    }
}
