package me.topchetoeu.animatedchunks.easing;

import java.util.Map;

public class ElasticEase implements Ease {
    private float steepness = 1;
    private int periods = 3;

    public int getPeriods() {
        return periods;
    }
    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public float getSteepness() {
        return steepness;
    }
    public void setSteepness(float steepness) {
        this.steepness = steepness;
    }

    @Override
    public Map<String, Float> uniforms() {
        return Map.of("ease_s", steepness, "ease_p", (float)periods);
    }

    @Override
    public String statement() {
        return
            "tmp0 = pow(2, -ease_s * x) * (1 - x);" +
            "tmp1 = sin(6.28 * ease_p * x - 1.57);" +
            "t = tmp0 * tmp1 + 1;";

        // float amplitude = (float)Math.pow(2, -steepness * x) * (1 - x);
        // float wave = (float)Math.sin(2 * Math.PI * periods * x - Math.PI / 2);

        // return amplitude * wave + 1;
    }

}
