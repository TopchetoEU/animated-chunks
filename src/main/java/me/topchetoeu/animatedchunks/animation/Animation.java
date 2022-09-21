package me.topchetoeu.animatedchunks.animation;

import net.minecraft.client.util.math.MatrixStack;

public interface Animation {
    /**
     * Animations using the currently set ease
     * @param progress The point at which the animation currently is (a value between 0 and 1)
     * @param matrices The matrix stack used for rendering
     * @param chunkX The current chunk's x (in blocks) which is animated
     * @param chunkY The current chunk's y (in blocks) which is animated
     * @param chunkZ The current chunk's z (in blocks) which is animated
     * @param playerX The player's x (in blocks)
     * @param playerY The player's y (in blocks)
     * @param playerZ The player's z (in blocks)
     */
    void animate(float progress, MatrixStack matrices, int chunkX, int chunkY, int chunkZ, float playerX, float playerY, float playerZ);
}
