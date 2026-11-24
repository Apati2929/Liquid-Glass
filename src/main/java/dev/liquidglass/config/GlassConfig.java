package dev.liquidglass.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Tiny settings store, saved to config/liquid-glass.properties.
 * Everything here is client-side and purely visual.
 */
public final class GlassConfig {

    public static final String[] STYLE_NAMES = {"White", "Cyan", "Electric", "Fire", "Green", "Purple"};
    public static final String[] WEATHER_NAMES = {"Off", "Clear", "Rain", "Storm"};

    /** Ring of particles when the local player jumps. */
    public static boolean jumpCircles = true;
    /** Index into {@link #STYLE_NAMES}. */
    public static int jumpStyle = 0;
    /** Hides your own arm-swing animation (attacks still work normally). */
    public static boolean noSwing = false;
    /** Index into {@link #WEATHER_NAMES}; 0 = leave the weather alone. */
    public static int weather = 0;

    private GlassConfig() {
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("liquid-glass.properties");
    }

    public static void load() {
        Path path = file();
        if (!Files.exists(path)) {
            return;
        }
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            p.load(in);
        } catch (IOException e) {
            return;
        }
        jumpCircles = Boolean.parseBoolean(p.getProperty("jumpCircles", "true"));
        noSwing = Boolean.parseBoolean(p.getProperty("noSwing", "false"));
        jumpStyle = clampIndex(parseInt(p.getProperty("jumpStyle"), 0), STYLE_NAMES.length);
        weather = clampIndex(parseInt(p.getProperty("weather"), 0), WEATHER_NAMES.length);
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("jumpCircles", Boolean.toString(jumpCircles));
        p.setProperty("noSwing", Boolean.toString(noSwing));
        p.setProperty("jumpStyle", Integer.toString(jumpStyle));
        p.setProperty("weather", Integer.toString(weather));
        try (OutputStream out = Files.newOutputStream(file())) {
            p.store(out, "Liquid Glass settings");
        } catch (IOException ignored) {
            // settings simply won't persist; nothing else depends on this
        }
    }

    /** Forced rain strength for the client, or null to leave vanilla behaviour. */
    public static Float forcedRain() {
        return switch (weather) {
            case 1 -> 0.0f;
            case 2, 3 -> 1.0f;
            default -> null;
        };
    }

    /** Forced thunder strength for the client, or null to leave vanilla behaviour. */
    public static Float forcedThunder() {
        return switch (weather) {
            case 1, 2 -> 0.0f;
            case 3 -> 1.0f;
            default -> null;
        };
    }

    private static int parseInt(String s, int fallback) {
        try {
            return s == null ? fallback : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int clampIndex(int v, int size) {
        return v < 0 || v >= size ? 0 : v;
    }
}
