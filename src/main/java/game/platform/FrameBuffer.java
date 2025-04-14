package game.platform;

public interface FrameBuffer {
    int getWidth();
    int getHeight();
    Renderer createRenderer();

    void dispose();
}
