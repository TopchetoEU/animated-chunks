package me.topchetoeu.animatedchunks.easing;

public class QuadraticEase implements Ease {
    @Override
    public float ease(float x) {
        return x * x;
    }
}
