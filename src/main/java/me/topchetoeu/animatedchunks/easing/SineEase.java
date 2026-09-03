package me.topchetoeu.animatedchunks.easing;

public class SineEase implements Ease {
    public String statement() {
        return "t = sin(x * 1.57);";
    }
}
