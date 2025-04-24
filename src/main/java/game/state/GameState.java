package game.state;

import game.Game;
import game.graphics.Renderer;
import game.graphics.background.Background;
import game.graphics.background.BackgroundFactory;
import game.gui.input.GuiEventState;
import game.gui.input.GuiMouseCapture;
import game.input.MouseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class GameState {
    @Getter
    protected final Game game;
    private final List<Background> backgrounds = new ArrayList<>();

    public void addBackground(BackgroundFactory factory) {
        Background background = factory.create(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        if (background != null) {
            backgrounds.add(background);
        }
    }

    public void onEnter() {
    }

    public final void dispatchMouseEvent(MouseEvent event) {
        if (GuiMouseCapture.dispatchToCapturedComponent(event) == GuiEventState.CONSUMED) {
            return; // event was handled by the captured component
        }

        onMouseEvent(event);
    }

    public void onMouseEvent(MouseEvent event) {
        // Default implementation does nothing
    }


    public void onKeyPressed(int keyCode) {
    }

    public void onKeyReleased(int keyCode) {
    }

    public void onKeyTyped(char keyChar) {
    }

    public final void update(Duration delta) {
        for (Background background : backgrounds) {
            background.update(delta);
        }

        onUpdate(delta);
    }

    public final void render(Renderer renderer) {
        // Clear the screen
        renderer.setColor(new Color(0, 0, 30));
        renderer.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        for (Background background : backgrounds) {
            background.render(renderer);
        }

        onRender(renderer);
    }

    public abstract void onUpdate(Duration delta);

    public abstract void onRender(Renderer renderer);

    public void onExit() {
    }
}
