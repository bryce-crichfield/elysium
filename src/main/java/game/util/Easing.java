package game.util;

/**
 * Functional interface for easing functions used in animations.
 */
@FunctionalInterface
public interface Easing {
    /**
     * Linear interpolation (no easing)
     */
    static Easing linear() {
        return (start, end, duration, elapsed) -> {
            float t = Math.min(1.0f, elapsed / duration);
            return start + (end - start) * t;
        };
    }

    /**
     * Quadratic ease in: accelerating from zero velocity
     */
    static Easing easeIn() {
        return (start, end, duration, elapsed) -> {
            float t = Math.min(1.0f, elapsed / duration);
            return start + (end - start) * t * t;
        };
    }

    /**
     * Quadratic ease out: decelerating to zero velocity
     */
    static Easing easeOut() {
        return (start, end, duration, elapsed) -> {
            float t = Math.min(1.0f, elapsed / duration);
            return start + (end - start) * (-(t * (t - 2)));
        };
    }

    /**
     * Quadratic ease in-out: acceleration until halfway, then deceleration
     */
    static Easing easeInOut() {
        return (start, end, duration, elapsed) -> {
            float t = Math.min(1.0f, elapsed / duration);
            float change = end - start;
            t *= 2;

            if (t < 1) return start + change / 2 * t * t;

            t -= 1;
            return start + change / 2 * (t * (t - 2) - 1);
        };
    }

    /**
     * Cubic ease in: accelerating from zero velocity
     */
    static Easing cubicEaseIn() {
        return (start, end, duration, elapsed) -> {
            float t = Math.min(1.0f, elapsed / duration);
            return start + (end - start) * t * t * t;
        };
    }

    /**
     * Cubic ease out: decelerating to zero velocity
     */
    static Easing cubicEaseOut() {
        return (start, end, duration, elapsed) -> {
            float t = Math.min(1.0f, elapsed / duration);
            t = t - 1;
            return start + (end - start) * (t * t * t + 1);
        };
    }

    /**
     * Calculates the eased value based on start, end, duration, and elapsed time.
     *
     * @param start    Starting value
     * @param end      Ending value
     * @param duration Total duration
     * @param elapsed  Elapsed time
     * @return The eased value
     */
    float ease(float start, float end, float duration, float elapsed);
}