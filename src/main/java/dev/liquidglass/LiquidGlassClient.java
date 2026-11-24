package dev.liquidglass;

import dev.liquidglass.config.GlassConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point. The mod is purely visual: the only thing it does is replace the
 * vanilla title screen (see MinecraftMixin) with a liquid-glass one.
 */
public class LiquidGlassClient implements ClientModInitializer {
    public static final String MOD_ID = "liquid-glass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        GlassConfig.load();
        LOGGER.info("Liquid Glass loaded (visual-only client mod)");
    }
}
