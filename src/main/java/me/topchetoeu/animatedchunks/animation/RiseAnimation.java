package me.topchetoeu.animatedchunks.animation;

import java.util.Map;

public final class RiseAnimation implements Animation {
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
    public String statement() {
        return "pos += vec3(0, animation_f * (t - 1));";
    }

    public RiseAnimation(float offset) {
        this.offset = offset;
    }
    public RiseAnimation() {
        offset = 50;
    }
}
