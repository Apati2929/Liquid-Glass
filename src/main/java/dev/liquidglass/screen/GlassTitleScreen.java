package dev.liquidglass.screen;

import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

/**
 * Liquid-glass main menu. It opens the same vanilla screens as the original menu
 * (world list, server list, options) plus the mod's own Visuals screen, and
 * every screen returns here when closed.
 */
public class GlassTitleScreen extends GlassScreenBase {

    public GlassTitleScreen() {
        super(Component.literal("Liquid Glass"));
    }

    @Override
    protected void init() {
        layout("LIQUID GLASS", 5);

        addRow(0, Component.translatable("menu.singleplayer"),
                b -> this.minecraft.setScreen(new SelectWorldScreen(this)));
        addRow(1, Component.translatable("menu.multiplayer"),
                b -> this.minecraft.setScreen(new JoinMultiplayerScreen(this)));
        addRow(2, Component.literal("Visuals"),
                b -> this.minecraft.setScreen(new GlassSettingsScreen(this)));
        addRow(3, Component.translatable("menu.options"),
                b -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options)));
        addRow(4, Component.translatable("menu.quit"),
                b -> this.minecraft.stop());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
