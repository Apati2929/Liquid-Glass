package dev.liquidglass.screen;

import dev.liquidglass.render.BlockFont;
import dev.liquidglass.render.Glass;
import dev.liquidglass.widget.GlassButton;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Shared look for all glass screens: aurora background, big glass heading,
 * a frosted card and a column of glass buttons. Subclasses call
 * {@link #layout(String, int)} from init() and then {@link #addRow}.
 */
public abstract class GlassScreenBase extends Screen {

    private static final String FOOTER = "Liquid Glass · visual only, no gameplay changes";

    private final List<GlassButton> glassButtons = new ArrayList<>();
    private final long openedAt = System.currentTimeMillis();

    private String heading = "";
    private int headingCell;
    private int headingY;
    private int cardX;
    private int cardY;
    private int cardW;
    private int cardH;
    private int btnX;
    private int btnY;
    private int btnW;
    private int btnH;
    private int step;

    protected GlassScreenBase(Component title) {
        super(title);
    }

    /** Computes the centred layout for {@code rows} buttons and resets the button list. */
    protected final void layout(String headingText, int rows) {
        glassButtons.clear();
        this.heading = headingText;

        int cell = this.height >= 300 ? 4 : 3;
        int h = 24;
        int gap = 8;
        int pad = 14;
        int space = 24;
        int total = 7 * cell + space + rows * h + (rows - 1) * gap + pad * 2;
        if (total > this.height - 16) { // small window / big GUI scale: compact variant
            cell = 2;
            h = 20;
            gap = 4;
            pad = 10;
            space = 14;
            total = 7 * cell + space + rows * h + (rows - 1) * gap + pad * 2;
        }

        btnW = Math.min(200, this.width - 60);
        btnH = h;
        step = h + gap;
        cardW = btnW + pad * 2;
        cardH = rows * h + (rows - 1) * gap + pad * 2;
        headingCell = cell;
        headingY = Math.max(8, (this.height - total) / 2);
        cardX = (this.width - cardW) / 2;
        cardY = headingY + 7 * cell + space;
        btnX = cardX + pad;
        btnY = cardY + pad;
    }

    protected final GlassButton addRow(int index, Component label, Button.OnPress action) {
        GlassButton button = new GlassButton(btnX, btnY + index * step, btnW, btnH, label, action);
        glassButtons.add(button);
        // addWidget = input + keyboard focus, but NOT drawn by the vanilla pass:
        // the buttons are drawn manually below, on top of our own background.
        this.addWidget(button);
        return button;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Deliberately no super call: this screen draws its own background and widgets.
        float t = (System.currentTimeMillis() - openedAt) / 1000f;
        float appear = ease(Math.min(1f, t / 0.7f));

        try {
            Glass.setAlpha(1f);
            Glass.aurora(graphics, this.width, this.height, t);

            Glass.setAlpha(appear);
            int slide = Math.round((1f - appear) * 10f);

            BlockFont.drawCentered(graphics, heading, this.width / 2, headingY - slide, headingCell);
            Glass.panel(graphics, cardX, cardY + slide, cardW, cardH, 16, 0f);

            for (GlassButton button : glassButtons) {
                button.drawGlass(graphics, this.font, mouseX, mouseY);
            }

            Glass.text(graphics, this.font, FOOTER, 8, this.height - 14, 0x88FFFFFF, false);
        } finally {
            Glass.setAlpha(1f);
        }
    }

    private static float ease(float x) {
        float inv = 1f - x;
        return 1f - inv * inv * inv;
    }
}
