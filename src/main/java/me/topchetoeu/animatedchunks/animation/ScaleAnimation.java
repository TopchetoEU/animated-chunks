package me.topchetoeu.animatedchunks.animation;

import java.util.Map;

public class ScaleAnimation implements Animation {
    public static float xOffset = 8, yOffset = 8, zOffset = 8;

    private boolean scaleY = true;

    public boolean isScalingY() {
        return scaleY;
    }
    public void setScalingY(boolean scaleY) {
        this.scaleY = scaleY;
    }

    @Override
    public Map<String, Float> uniforms() {
        return Map.of("animation_ox", xOffset, "animation_oy", yOffset, "animation_oz", zOffset, "animation_sy", scaleY ? 1f : 0f);
    }

    @Override
    public String statement() {
        return
            "tmp8 = vec3(animation_ox, animation_oy, animation_oz);" +
            "tmp9 = vec3(t);" +
            "if (animation_sy < .5f) tmp9.y = 0;" +
            "pos += tmp8;" +
            "pos *= tmp9;" +
            "pos -= tmp8;";
    }

    // @Override
    // public void animate(float progress, MatrixStack matrices, int chunkX, int chunkY, int chunkZ, float playerX, float playerY, float playerZ) {
    //     float scaleX = progress;
    //     float scaleZ = progress;

    //     matrices.translate(xOffset, yOffset, zOffset);
    //     matrices.scale(scaleX, 1, scaleZ);
    //     matrices.translate(-xOffset, -yOffset, -zOffset);
    // }

    public ScaleAnimation() { }
}
