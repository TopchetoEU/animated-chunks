package me.topchetoeu.animatedchunks.animation;

import net.minecraft.client.util.math.MatrixStack;

public class FallAnimation implements Animation {
    private float offset;

    public float getOffset() {
        return offset;
    }
    public void setOffset(float offset) {
        this.offset = offset;
    }

    @Override
    public void animate(float progress, MatrixStack matrices, int chunkX, int chunkY, int chunkZ, float playerX, float playerY, float playerZ) {
        animate(progress, matrices);
    }
    public void animate(float progress, MatrixStack matrices) {
        matrices.translate(0, offset * (1 - progress), 0);
    }

    public FallAnimation(float offset) {
        this.offset = offset;
    }
    public FallAnimation() {
        offset = 50;
    }
}
