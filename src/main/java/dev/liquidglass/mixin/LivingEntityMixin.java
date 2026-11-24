package dev.liquidglass.mixin;

import dev.liquidglass.config.GlassConfig;
import dev.liquidglass.effects.JumpCircles;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Both hooks act only on the local (client) player, never on the integrated
 * server's copy of the player or on other entities. Both use require = 0: if a
 * method is ever renamed, the feature quietly turns off instead of crashing.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "jumpFromGround", at = @At("HEAD"), require = 0)
    private void liquidglass$jumpCircle(CallbackInfo ci) {
        Object self = this;
        if (GlassConfig.jumpCircles && self instanceof LocalPlayer player) {
            JumpCircles.spawn(player);
        }
    }

    @Inject(method = "getAttackAnim", at = @At("HEAD"), cancellable = true, require = 0)
    private void liquidglass$noSwing(float partialTick, CallbackInfoReturnable<Float> cir) {
        Object self = this;
        if (GlassConfig.noSwing && self instanceof LocalPlayer) {
            // Only the animation shown on your screen is hidden; the attack itself is untouched.
            cir.setReturnValue(0.0f);
        }
    }
}
