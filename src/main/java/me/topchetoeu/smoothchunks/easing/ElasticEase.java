package me.topchetoeu.smoothchunks.easing;

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
    public float ease(float x) {
        float amplitude = (float)Math.pow(2, -steepness * x) * (1 - x);
        float wave = (float)Math.sin(2 * Math.PI * periods * x - Math.PI / 2);

        return amplitude * wave + 1;
    }

}
