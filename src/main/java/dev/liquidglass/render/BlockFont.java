package dev.liquidglass.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Tiny 5x7 pixel font used for the big glass title. Only the letters needed for
 * the title are defined; unknown characters are drawn as blank space.
 */
public final class BlockFont {

    private BlockFont() {
    }

    private static String[] glyph(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'L' -> new String[]{"X....", "X....", "X....", "X....", "X....", "X....", "XXXXX"};
            case 'I' -> new String[]{"XXXXX", "..X..", "..X..", "..X..", "..X..", "..X..", "XXXXX"};
            case 'Q' -> new String[]{".XXX.", "X...X", "X...X", "X...X", "X.X.X", "X..X.", ".XX.X"};
            case 'U' -> new String[]{"X...X", "X...X", "X...X", "X...X", "X...X", "X...X", ".XXX."};
            case 'D' -> new String[]{"XXXX.", "X...X", "X...X", "X...X", "X...X", "X...X", "XXXX."};
            case 'G' -> new String[]{".XXX.", "X...X", "X....", "X.XXX", "X...X", "X...X", ".XXX."};
            case 'A' -> new String[]{".XXX.", "X...X", "X...X", "XXXXX", "X...X", "X...X", "X...X"};
            case 'V' -> new String[]{"X...X", "X...X", "X...X", "X...X", "X...X", ".X.X.", "..X.."};
            case 'S' -> new String[]{".XXXX", "X....", "X....", ".XXX.", "....X", "....X", "XXXX."};
            default -> null;
        };
    }

    /** Width in px of the text drawn with the given cell size. */
    public static int width(String text, int cell) {
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
            w += glyph(text.charAt(i)) == null ? 4 * cell : 6 * cell;
        }
        return Math.max(0, w - cell); // no trailing gap
    }

    /** Draws the text horizontally centred on {@code cx}, top edge at {@code y}. */
    public static void drawCentered(GuiGraphicsExtractor g, String text, int cx, int y, int cell) {
        int x = cx - width(text, cell) / 2;
        for (int i = 0; i < text.length(); i++) {
            String[] rows = glyph(text.charAt(i));
            if (rows == null) {
                x += 4 * cell;
                continue;
            }
            drawGlyph(g, rows, x, y, cell);
            x += 6 * cell;
        }
    }

    private static void drawGlyph(GuiGraphicsExtractor g, String[] rows, int x, int y, int cell) {
        int topColor = Glass.argb(235, 255, 255, 255);
        int bottomColor = Glass.argb(190, 150, 215, 255);

        // pass 1: soft coloured glow behind the letter
        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < rows[row].length(); col++) {
                if (rows[row].charAt(col) == 'X') {
                    int px = x + col * cell;
                    int py = y + row * cell;
                    Glass.fill(g, px - 1, py - 1, px + cell + 1, py + cell + 1, Glass.argb(26, 110, 190, 255));
                    Glass.fill(g, px + 1, py + 2, px + cell + 1, py + cell + 2, Glass.argb(70, 0, 0, 20));
                }
            }
        }
        // pass 2: glass body, vertical gradient, bright edge on cells with nothing above
        for (int row = 0; row < rows.length; row++) {
            int color = Glass.lerp(topColor, bottomColor, row / (float) (rows.length - 1));
            for (int col = 0; col < rows[row].length(); col++) {
                if (rows[row].charAt(col) != 'X') {
                    continue;
                }
                int px = x + col * cell;
                int py = y + row * cell;
                Glass.fill(g, px, py, px + cell, py + cell, color);
                boolean openAbove = row == 0 || rows[row - 1].charAt(col) != 'X';
                if (openAbove) {
                    Glass.fill(g, px, py, px + cell, py + 1, Glass.argb(255, 255, 255, 255));
                }
            }
        }
    }
}
