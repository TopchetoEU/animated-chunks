package me.topchetoeu.smoothchunks.animation;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;

public class FlyInAnimation implements Animation {
    private float offset;

    public float getOffset() {
        return offset;
    }
    public void setOffset(float offset) {
        this.offset = offset;
    }


    @Override
    public void animate(float progress, MatrixStack matrices, int chunkX, int chunkY, int chunkZ, float playerX, float playerY, float playerZ) {
        Vec2f direction = new Vec2f(playerX, playerZ).add(new Vec2f(-chunkX, -chunkZ)).normalize().multiply(-offset);

        matrices.translate(direction.x * (1 - progress), 0, direction.y * (1 - progress));
    }

    public FlyInAnimation() {
        offset = 50;
    }
}
