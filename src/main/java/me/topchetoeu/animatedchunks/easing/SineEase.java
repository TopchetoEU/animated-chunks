package me.topchetoeu.animatedchunks.easing;

public class SineEase implements Ease {
    @Override
    public float ease(float x) {
        return (float)Math.sin(x * Math.PI / 2);
    }
}
