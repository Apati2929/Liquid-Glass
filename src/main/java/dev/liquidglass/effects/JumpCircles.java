package dev.liquidglass.effects;

import dev.liquidglass.config.GlassConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

/**
 * "Jump circle": an expanding ring of particles around your feet when you jump.
 * It uses the game's normal particle system, so it is client-only and purely cosmetic.
 */
public final class JumpCircles {

    private JumpCircles() {
    }

    private static ParticleOptions style() {
        return switch (GlassConfig.jumpStyle) {
            case 1 -> ParticleTypes.SOUL_FIRE_FLAME;
            case 2 -> ParticleTypes.ELECTRIC_SPARK;
            case 3 -> ParticleTypes.FLAME;
            case 4 -> ParticleTypes.HAPPY_VILLAGER;
            case 5 -> ParticleTypes.WITCH;
            default -> ParticleTypes.END_ROD;
        };
    }

    public static void spawn(LocalPlayer player) {
        Level level = player.level();
        ParticleOptions particle = style();
        double x = player.getX();
        double y = player.getY() + 0.08;
        double z = player.getZ();

        ring(level, particle, x, y, z, 36, 0.35, 0.14);
        ring(level, particle, x, y, z, 20, 0.20, 0.07);
    }

    private static void ring(Level level, ParticleOptions particle, double x, double y, double z,
                             int count, double radius, double speed) {
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2.0 * i) / count;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            level.addParticle(particle, x + cos * radius, y, z + sin * radius, cos * speed, 0.0, sin * speed);
        }
    }
}
