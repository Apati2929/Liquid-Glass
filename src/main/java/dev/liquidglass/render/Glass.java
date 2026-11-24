package dev.liquidglass.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Drawing helpers for the "liquid glass" look. Everything is built from the three
 * most basic GUI primitives (fill, fillGradient, text), so it does not depend on
 * shaders or internal render pipelines.
 *
 * All draw calls go through {@link #fill}, {@link #gradient} and {@link #text},
 * which multiply alpha by a global factor, used for the fade-in animation.
 */
public final class Glass {

    private static float globalAlpha = 1f;

    private Glass() {
    }

    public static void setAlpha(float alpha) {
        globalAlpha = Math.max(0f, Math.min(1f, alpha));
    }

    // ------------------------------------------------------------------ colours

    private static int clamp255(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }

    public static int argb(int a, int r, int g, int b) {
        return (clamp255(a) << 24) | (clamp255(r) << 16) | (clamp255(g) << 8) | clamp255(b);
    }

    public static int withRgb(int rgb, int alpha) {
        return (clamp255(alpha) << 24) | (rgb & 0xFFFFFF);
    }

    public static int scaleAlpha(int color, float factor) {
        int a = Math.round(((color >>> 24) & 0xFF) * factor);
        return (clamp255(a) << 24) | (color & 0xFFFFFF);
    }

    public static int lerp(int c1, int c2, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int a = Math.round(((c1 >>> 24) & 0xFF) + ((((c2 >>> 24) & 0xFF) - ((c1 >>> 24) & 0xFF)) * t));
        int r = Math.round(((c1 >> 16) & 0xFF) + ((((c2 >> 16) & 0xFF) - ((c1 >> 16) & 0xFF)) * t));
        int g = Math.round(((c1 >> 8) & 0xFF) + ((((c2 >> 8) & 0xFF) - ((c1 >> 8) & 0xFF)) * t));
        int b = Math.round((c1 & 0xFF) + (((c2 & 0xFF) - (c1 & 0xFF)) * t));
        return argb(a, r, g, b);
    }

    // --------------------------------------------------------------- primitives

    public static void fill(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int color) {
        if (x2 <= x1 || y2 <= y1) {
            return;
        }
        int c = scaleAlpha(color, globalAlpha);
        if ((c >>> 24) == 0) {
            return;
        }
        g.fill(x1, y1, x2, y2, c);
    }

    public static void gradient(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int top, int bottom) {
        if (x2 <= x1 || y2 <= y1) {
            return;
        }
        g.fillGradient(x1, y1, x2, y2, scaleAlpha(top, globalAlpha), scaleAlpha(bottom, globalAlpha));
    }

    public static void text(GuiGraphicsExtractor g, Font font, String s, int x, int y, int color, boolean shadow) {
        int c = scaleAlpha(color, globalAlpha);
        if ((c >>> 24) < 8) {
            return;
        }
        g.text(font, s, x, y, c, shadow);
    }

    // ------------------------------------------------------------ rounded shapes

    /** Horizontal inset (in px, fractional) of a rounded rectangle at the given row. */
    private static float insetAt(int row, int h, int r) {
        if (r <= 0) {
            return 0f;
        }
        float dy;
        if (row < r) {
            dy = r - (row + 0.5f);
        } else if (row >= h - r) {
            dy = (row + 0.5f) - (h - r);
        } else {
            return 0f;
        }
        float inner = r * r - dy * dy;
        if (inner < 0f) {
            inner = 0f;
        }
        return r - (float) Math.sqrt(inner);
    }

    private static int clampRadius(int r, int w, int h) {
        return Math.max(0, Math.min(r, Math.min(w, h) / 2));
    }

    private static void roundedRow(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int row, int color) {
        float inset = insetAt(row, h, r);
        int ix = (int) Math.ceil(inset);
        fill(g, x + ix, y + row, x + w - ix, y + row + 1, color);
        float coverage = ix - inset;
        if (ix > 0 && coverage > 0.05f) {
            // one anti-aliased pixel on each side
            int edge = scaleAlpha(color, coverage);
            fill(g, x + ix - 1, y + row, x + ix, y + row + 1, edge);
            fill(g, x + w - ix, y + row, x + w - ix + 1, y + row + 1, edge);
        }
    }

    /** Rounded rectangle filled with a vertical gradient (top -> bottom). */
    public static void roundedRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int top, int bottom) {
        if (w <= 0 || h <= 0) {
            return;
        }
        r = clampRadius(r, w, h);
        for (int row = 0; row < Math.min(r, h); row++) {
            roundedRow(g, x, y, w, h, r, row, rowColor(top, bottom, row, h));
        }
        int midStart = r;
        int midEnd = h - r;
        if (midEnd > midStart) {
            gradient(g, x, y + midStart, x + w, y + midEnd,
                    rowColor(top, bottom, midStart, h), rowColor(top, bottom, midEnd - 1, h));
        }
        for (int row = Math.max(h - r, r); row < h; row++) {
            roundedRow(g, x, y, w, h, r, row, rowColor(top, bottom, row, h));
        }
    }

    private static int rowColor(int top, int bottom, int row, int h) {
        return h <= 1 ? top : lerp(top, bottom, row / (float) (h - 1));
    }

    /** 1px rounded outline. */
    public static void roundedOutline(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        if (w < 4 || h < 4) {
            return;
        }
        r = clampRadius(r, w, h);
        int ri = Math.max(0, r - 1);
        for (int row = 0; row < h; row++) {
            int o = (int) Math.ceil(insetAt(row, h, r));
            if (row == 0 || row == h - 1) {
                fill(g, x + o, y + row, x + w - o, y + row + 1, color);
                continue;
            }
            int i = 1 + (int) Math.ceil(insetAt(row - 1, h - 2, ri));
            if (i > o) {
                fill(g, x + o, y + row, x + i, y + row + 1, color);
                fill(g, x + w - i, y + row, x + w - o, y + row + 1, color);
            } else {
                fill(g, x + o, y + row, x + o + 1, y + row + 1, color);
                fill(g, x + w - o - 1, y + row, x + w - o, y + row + 1, color);
            }
        }
    }

    // ------------------------------------------------------------------- panels

    /**
     * A frosted-glass panel: soft shadow, translucent body, top gloss, rim light.
     *
     * @param glow 0..1, hover / focus intensity
     */
    public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, float glow) {
        glow = Math.max(0f, Math.min(1f, glow));

        // soft drop shadow
        for (int i = 3; i >= 1; i--) {
            int a = i == 3 ? 10 : (i == 2 ? 15 : 22);
            roundedRect(g, x - i, y - i + 2, w + 2 * i, h + 2 * i, r + i, argb(a, 0, 0, 0), argb(a, 0, 0, 0));
        }

        // body: bright, slightly warm-white at the top, cool blue at the bottom
        int bodyTop = argb(56 + Math.round(glow * 34), 255, 255, 255);
        int bodyBottom = argb(24 + Math.round(glow * 30), 175, 215, 255);
        roundedRect(g, x, y, w, h, r, bodyTop, bodyBottom);

        // gloss on the upper half
        roundedRect(g, x + 2, y + 2, w - 4, Math.max(2, h / 2), Math.max(0, r - 2),
                argb(52 + Math.round(glow * 26), 255, 255, 255), argb(0, 255, 255, 255));

        // rim
        roundedOutline(g, x, y, w, h, r, argb(115 + Math.round(glow * 90), 255, 255, 255));

        // specular highlight along the top edge, faint cyan caustic along the bottom
        int inset = Math.max(r, 3);
        fill(g, x + inset, y + 1, x + w - inset, y + 2, argb(150 + Math.round(glow * 70), 255, 255, 255));
        fill(g, x + inset, y + h - 2, x + w - inset, y + h - 1, argb(34 + Math.round(glow * 40), 140, 230, 255));
    }

    // --------------------------------------------------------------- background

    /** Animated aurora: deep gradient with slowly drifting soft colour blobs. */
    public static void aurora(GuiGraphicsExtractor g, int w, int h, float t) {
        gradient(g, 0, 0, w, h, 0xFF070A1F, 0xFF150B31);

        float m = Math.min(w, h);
        blob(g, w * (0.22f + 0.08f * (float) Math.sin(t * 0.37)),
                h * (0.28f + 0.10f * (float) Math.cos(t * 0.29)), m * 0.55f, 0x5B5BFF);
        blob(g, w * (0.80f + 0.07f * (float) Math.cos(t * 0.31)),
                h * (0.32f + 0.10f * (float) Math.sin(t * 0.41)), m * 0.50f, 0x16D5E6);
        blob(g, w * (0.55f + 0.10f * (float) Math.sin(t * 0.23)),
                h * (0.85f + 0.06f * (float) Math.cos(t * 0.35)), m * 0.60f, 0xFF4FA3);
        blob(g, w * (0.10f + 0.06f * (float) Math.cos(t * 0.27)),
                h * (0.82f + 0.05f * (float) Math.sin(t * 0.33)), m * 0.40f, 0x7C3AED);

        // gentle vignette
        gradient(g, 0, h / 2, w, h, argb(0, 0, 0, 0), argb(90, 0, 0, 0));
    }

    private static void blob(GuiGraphicsExtractor g, float cx, float cy, float radius, int rgb) {
        final int layers = 14; // many faint layers = smooth falloff, no visible rings
        int color = withRgb(rgb, 7);
        for (int i = 0; i < layers; i++) {
            float rr = radius * (1f - i / (float) layers);
            disc(g, Math.round(cx), Math.round(cy), Math.round(rr), color);
        }
    }

    private static void disc(GuiGraphicsExtractor g, int cx, int cy, int radius, int color) {
        final int strip = 6;
        for (int dy = -radius; dy < radius; dy += strip) {
            float yy = dy + strip / 2f;
            float inner = radius * radius - yy * yy;
            if (inner <= 0f) {
                continue;
            }
            int half = (int) Math.sqrt(inner);
            fill(g, cx - half, cy + dy, cx + half, cy + dy + strip, color);
        }
    }
}
