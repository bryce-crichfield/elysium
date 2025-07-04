package client.core.util;

import java.time.Duration;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.function.Supplier;

public enum Util {
  ;

  public static float perSecond(Duration delta) {
    return delta.toNanos() / 1e9f;
  }

  public static int wrap(int value, int lower, int upper) {
    try {
      return Math.floorMod(value - lower, upper - lower) + lower;
    } catch (ArithmeticException e) {
      return value;
    }
  }

  public static float wrap(float value, float lower, float upper) {
    return (value - lower) % (upper - lower) + lower;
  }

  public static int clamp(int value, int lower, int upper) {
    return Math.max(lower, Math.min(value, upper));
  }

  public static float clamp(float value, float lower, float upper) {
    return Math.max(lower, Math.min(value, upper));
  }

  public static int random(int lower, int upper) {
    return (int) (Math.random() * (upper - lower)) + lower;
  }

  public static float random(float min, float max, float skew, float bias) {
    float range = max - min;
    float mid = min + range / 2.0f;
    float unitGaussian = (float) new Random().nextGaussian();
    float biasFactor = (float) Math.exp(bias);
    float retval =
        (float)
            (mid + (range * (biasFactor / (biasFactor + Math.exp(-unitGaussian / skew)) - 0.5)));
    return retval;
  }

  public static float map(float size, float lower1, float upper1, float lower2, float upper2) {
    return lower2 + (size - lower1) * (upper2 - lower2) / (upper1 - lower1);
  }

  public static float random(float lower, float upper) {
    return (float) (Math.random() * (upper - lower)) + lower;
  }

  public static float distance(float x1, float y1, float x2, float y2) {
    return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  public static float lerp(float a, float b, float t) {
    return a + (b - a) * t;
  }

  public static float easeIn(float start, float end, float duration, float elapsed) {
    float t = elapsed / duration;
    return start + (end - start) * t * t;
  }

  public static <T> Optional<T> optionalFromThrowable(Supplier<T> supplier) {
    try {
      return Optional.ofNullable(supplier.get());
    } catch (Throwable throwable) {
      return Optional.empty();
    }
  }

  public static void ensure(boolean check, String msg) {
    if (!check) {
      throw new RuntimeException("Optional was empty: " + msg);
    }
  }

  public static <T> T pure(Supplier<T> supplier) {
    return supplier.get();
  }

  public static <T> Optional<T> headOption(Queue<T> queue) {
    if (queue.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(queue.peek());
  }

  public static double rerange(
      Number value, Number oldMin, Number oldMax, Number newMin, Number newMax) {
    double oldMinD = oldMin.doubleValue();
    double oldMaxD = oldMax.doubleValue();
    double newMinD = newMin.doubleValue();
    double newMaxD = newMax.doubleValue();
    double oldRange = oldMaxD - oldMinD;
    double newRange = newMaxD - newMinD;
    if (oldRange == 0) {
      throw new IllegalArgumentException("Old range cannot be zero");
    }
    double newValue = ((value.doubleValue() - oldMinD) * newRange / oldRange) + newMinD;
    return newValue;
  }

  public static double clamp(double v, int i, int i1) {
    if (v < i) {
      return i;
    } else if (v > i1) {
      return i1;
    }
    return v;
  }
}
