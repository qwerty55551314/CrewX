package crewx.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public final class CrewXTransition {
    private static final long DURATION_MS = 260L;
    private static long startedAt;
    private static boolean active;
    private static boolean pushed;

    private CrewXTransition() {
    }

    public static void start() {
        startedAt = System.currentTimeMillis();
        active = true;
    }

    public static void cancel() {
        active = false;
        if (!pushed) return;
    }

    public static void beginGuiRender() {
        if (!active) return;
        float progress = progress();
        if (progress >= 1.0f) {
            active = false;
            return;
        }

        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        float eased = easeOutCubic(progress);
        float offset = (1.0f - eased) * resolution.getScaledWidth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(offset, 0.0f, 0.0f);
        pushed = true;
    }

    public static void endGuiRender() {
        if (!pushed) return;
        GlStateManager.popMatrix();
        pushed = false;
        if (progress() >= 1.0f) active = false;
    }

    private static float progress() {
        return Math.min(1.0f, (System.currentTimeMillis() - startedAt) / (float) DURATION_MS);
    }

    private static float easeOutCubic(float value) {
        float inverse = 1.0f - value;
        return 1.0f - inverse * inverse * inverse;
    }
}
