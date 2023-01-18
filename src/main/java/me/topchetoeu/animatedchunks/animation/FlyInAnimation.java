package me.topchetoeu.animatedchunks.animation;

import java.util.Map;

public class FlyInAnimation implements Animation {
    private float offset;

    public float getOffset() {
        return offset;
    }
    public void setOffset(float offset) {
        this.offset = offset;
    }

    public Map<String, Float> uniforms() {
        return Map.of("animation_f", offset);
    }
    @Override
    public String statement() {
        return "tmp8 = normalize(chunkPos.xz - playerPos.xz) * animation_f; tmp8 *= (1 - t); pos += tmp8;";
    }


    // @Override
    // public void animate(float progress, MatrixStack matrices, int chunkX, int chunkY, int chunkZ, float playerX, float playerY, float playerZ) {
    //     Vec2f direction = new Vec2f(playerX, playerZ).add(new Vec2f(-chunkX, -chunkZ)).normalize().multiply(-offset);

    //     matrices.translate(direction.x * (1 - progress), 0, direction.y * (1 - progress));
    // }

    public FlyInAnimation() {
        offset = 50;
    }
}
