package game.platform;

import game.Game;

public abstract class Window {
    protected final int width;
    protected final int height;
    protected final Game game;

    public Window(int width, int height, Game game) {
        this.game = game;
        this.width = width;
        this.height = height;
    }

    public abstract void onInit();
    public abstract void onRender(float updateTime, float deltaTime);
    public abstract void onClose();
}
