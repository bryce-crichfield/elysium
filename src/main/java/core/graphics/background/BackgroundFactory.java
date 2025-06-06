package core.graphics.background;

@FunctionalInterface
public interface BackgroundFactory {
    Background create(int screenWidth, int screenHeight);
}
