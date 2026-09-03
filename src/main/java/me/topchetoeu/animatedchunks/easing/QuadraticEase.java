package me.topchetoeu.animatedchunks.easing;

public class QuadraticEase implements Ease {
    public String statement() {
        return "t = x * x;";
    }
}
