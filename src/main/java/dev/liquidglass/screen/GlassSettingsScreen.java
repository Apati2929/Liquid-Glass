package dev.liquidglass.screen;

import dev.liquidglass.config.GlassConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Toggles for the visual effects. Changes apply immediately and are saved to disk. */
public class GlassSettingsScreen extends GlassScreenBase {

    private final Screen parent;

    public GlassSettingsScreen(Screen parent) {
        super(Component.literal("Visuals"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        layout("VISUALS", 5);

        addRow(0, jumpLabel(), b -> {
            GlassConfig.jumpCircles = !GlassConfig.jumpCircles;
            GlassConfig.save();
            b.setMessage(jumpLabel());
        });
        addRow(1, styleLabel(), b -> {
            GlassConfig.jumpStyle = (GlassConfig.jumpStyle + 1) % GlassConfig.STYLE_NAMES.length;
            GlassConfig.save();
            b.setMessage(styleLabel());
        });
        addRow(2, swingLabel(), b -> {
            GlassConfig.noSwing = !GlassConfig.noSwing;
            GlassConfig.save();
            b.setMessage(swingLabel());
        });
        addRow(3, weatherLabel(), b -> {
            GlassConfig.weather = (GlassConfig.weather + 1) % GlassConfig.WEATHER_NAMES.length;
            GlassConfig.save();
            b.setMessage(weatherLabel());
        });
        addRow(4, Component.translatable("gui.back"), b -> onClose());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private static Component jumpLabel() {
        return Component.literal("Jump Circles: " + onOff(GlassConfig.jumpCircles));
    }

    private static Component styleLabel() {
        return Component.literal("Circle Style: " + GlassConfig.STYLE_NAMES[GlassConfig.jumpStyle]);
    }

    private static Component swingLabel() {
        return Component.literal("No Swing: " + onOff(GlassConfig.noSwing));
    }

    private static Component weatherLabel() {
        return Component.literal("Weather: " + GlassConfig.WEATHER_NAMES[GlassConfig.weather]);
    }

    private static String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }
}
