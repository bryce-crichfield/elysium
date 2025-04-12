package game.platform;

public interface Transform {
    Transform copy();

    Transform translate(int x, int y);
    Transform inverse();
}
