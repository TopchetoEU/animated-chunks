package me.topchetoeu.animatedchunks.animation;

import net.minecraft.client.util.math.MatrixStack;

public class ScaleAnimation implements Animation {
    private boolean scaleY = true;
    private float xOffset = 8, yOffset = 8, zOffset = 8;

    public boolean isScalingY() {
        return scaleY;
    }
    public void setScalingY(boolean scaleY) {
        this.scaleY = scaleY;
    }

    public float getXOffset() {
        return xOffset;
    }
    public void setXOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getYOffset() {
        return yOffset;
    }
    public void setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }
    
    public float getZOffset() {
        return zOffset;
    }
    public void setZOffset(float zOffset) {
        this.zOffset = zOffset;
    }

    @Override
    public void animate(float progress, MatrixStack matrices, int chunkX, int chunkY, int chunkZ, float playerX, float playerY, float playerZ) {
        float scaleX = progress;
        float scaleZ = progress;

        matrices.translate(xOffset, yOffset, zOffset);
        matrices.scale(scaleX, 1, scaleZ);
        matrices.translate(-xOffset, -yOffset, -zOffset);
    }

    public ScaleAnimation() {

    }
}
