package me.topchetoeu.animatedchunks.easing;

import me.topchetoeu.animatedchunks.StatementFactory;

public interface Ease extends StatementFactory {
    /**
     * Converts a ease-in statement to an ease-out statement
     * Mathematically, the function is being "rotated" 180 degrees
     * @param func The ease statement to convert
     */
    public static Ease easeOut(Ease func) {
        return () -> "x = 1 - x; " + func.statement() + "x = 1 - x; t = 1 - t;";
        // return x -> 1 - func.ease(1 - x);
    }
    /**
     * Converts a ease statement to an ease in-out statement
     * Mathematically, the function is being split into two, where in the interval [0; 0.5), the ease-in function is being used, and in the other interval [0.5; 1], the ease-out function is being used
     * @param func The ease statement to convert
     * @return The ease in-out statement
     */
    public static Ease easeInOut(Ease func) {
        return () -> "x *= 2; if (x < 1f) { " + easeOut(func) + "} else { x -= 1; " + func.statement() + " } x /= 2;";
        // return x -> {
        //     float x2 = 2 * x;

        //     if (x < 0.5f) return (1 - func.ease(1 - x2)) / 2;
        //     else return (1 + func.ease(x2 - 1)) / 2;
        // };
    }
}
