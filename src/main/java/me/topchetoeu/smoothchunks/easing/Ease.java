package me.topchetoeu.smoothchunks.easing;

public interface Ease {
    /**
     * Converts the linear progress of an animation to an eased progress
     * @param x The progress of the animation being eased
     * @return The new, eased progress
     */
    float ease(float x);

    /**
     * Converts a function to an ease out version of itself.
     * Mathematically, the function is being "rotated" 180 degrees
     * @param func The function to convert
     * @return The ease out version of the function
     */
    public static Ease easeOut(Ease func) {
        return x -> 1 - func.ease(1 - x);
    }
    /**
     * Converts a function to an ease in-out version of itself.
     * Mathematically, the function is being split into two, where in the interval [0; 0.5], the ease-out function is being used, and in the other interval [0.5; 1], the ease-in function is being used
     * @param func The function to convert
     * @return The ease in-out version of the function
     */
    public static Ease easeInOut(Ease func) {
        return x -> {
            float x2 = 2 * x;

            if (x < 0.5f) return (1 - func.ease(1 - x2)) / 2;
            else return (1 + func.ease(x2 - 1)) / 2;
        };
    }
}
