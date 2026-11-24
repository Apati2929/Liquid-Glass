package dev.liquidglass.mixin;

import dev.liquidglass.screen.GlassTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Swaps the vanilla title screen for the glass one whenever the game opens it.
 * Two method names are listed on purpose: if the game ever routes screen changes
 * through a second method, whichever exists is hooked (a swapped screen is never
 * swapped twice, because GlassTitleScreen is not a TitleScreen).
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @ModifyVariable(method = {"setScreen", "setScreenAndShow"}, at = @At("HEAD"), argsOnly = true, require = 0)
    private Screen liquidglass$swapTitleScreen(Screen screen) {
        if (screen != null && screen.getClass() == TitleScreen.class) {
            return new GlassTitleScreen();
        }
        return screen;
    }
}
