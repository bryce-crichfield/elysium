package core.transition;

import core.util.Easing;

import java.awt.*;
import java.time.Duration;

public interface Transitions {
    static TransitionFactory fade(Duration duration, Color color, Easing easing) {
        return (source, target, callback) -> {
            FadeTransition transition = new FadeTransition(source, target, duration, color, easing);
            if (callback != null) {
                transition.setOnCompleteCallback(complete -> callback.run());
            }
            return transition;
        };
    }

//    static TransitionFactory wipe(Duration duration, WipeTransition.Direction direction) {
//        return (source, target, callback) -> {
//            WipeTransition transition = new WipeTransition(
//                    duration, source, target, direction);
//            if (callback != null) {
//                transition.setOnCompleteCallback(complete -> callback.run());
//            }
//            return transition;
//        };
//    }

    static TransitionFactory pixelate(Duration duration, int maxPixelSize, boolean isIntro) {
        return (source, target, callback) -> {
            PixelateTransition transition = new PixelateTransition(
                    duration, source, target, maxPixelSize, isIntro);
            if (callback != null) {
                transition.setOnCompleteCallback(complete -> callback.run());
            }
            return transition;
        };
    }
}
