package dev.liquidglass.widget;

import dev.liquidglass.render.Glass;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * A pill-shaped glass button. It is a normal vanilla Button (so clicking,
 * keyboard focus, sounds and narration all behave as usual); only the drawing is
 * custom, and the owning screen triggers it through {@link #drawGlass}.
 */
public final class GlassButton extends Button {

    private float hover;

    public GlassButton(int x, int y, int width, int height, Component label, OnPress onPress) {
        super(x, y, width, height, label, onPress, DEFAULT_NARRATION);
    }

    /** Draws the button. Called by the screen after it has drawn the background. */
    public void drawGlass(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        boolean over = this.active && mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        float target = (over || isFocused()) ? 1f : 0f;
        hover += (target - hover) * 0.22f;
        if (Math.abs(target - hover) < 0.01f) {
            hover = target;
        }

        Glass.panel(g, x, y, w, h, h / 2, hover);

        String label = getMessage().getString();
        int textX = x + (w - font.width(label)) / 2;
        int textY = y + (h - 8) / 2;
        int textColor = this.active ? 0xFFFFFFFF : 0x99FFFFFF;
        Glass.text(g, font, label, textX, textY, textColor, true);
    }
}
