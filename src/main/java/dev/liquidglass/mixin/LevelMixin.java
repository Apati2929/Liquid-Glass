package dev.liquidglass.mixin;

import dev.liquidglass.config.GlassConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * "Ambience": forces how the weather LOOKS on your screen (clear / rain / storm).
 * Only the client-side world is touched; the real weather on the server, and
 * gameplay, stay exactly as they are. Time of day is deliberately not changed,
 * because a custom time can make dark areas look bright, which some servers
 * treat like a fullbright advantage.
 */
@Mixin(Level.class)
public abstract class LevelMixin {

    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true, require = 0)
    private void liquidglass$rain(float partialTick, CallbackInfoReturnable<Float> cir) {
        Object self = this;
        if (self instanceof ClientLevel) {
            Float forced = GlassConfig.forcedRain();
            if (forced != null) {
                cir.setReturnValue(forced);
            }
        }
    }

    @Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true, require = 0)
    private void liquidglass$thunder(float partialTick, CallbackInfoReturnable<Float> cir) {
        Object self = this;
        if (self instanceof ClientLevel) {
            Float forced = GlassConfig.forcedThunder();
            if (forced != null) {
                cir.setReturnValue(forced);
            }
        }
    }
}
