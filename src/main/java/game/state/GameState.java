package game.state;

import game.Game;
import game.graphics.background.Background;
import game.graphics.background.BackgroundFactory;
import game.gui.input.GuiMouseManager;
import game.graphics.Renderer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import game.input.MouseEvent;
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
        if (GuiMouseManager.dispatchToCapturedComponent(event)) {
            return; // event was handled by the captured component
        }

        // Type-safe dispatch using instanceof
        if (event instanceof MouseEvent.Moved moved) {
            onMouseMoved(moved);
        } else if (event instanceof MouseEvent.Dragged dragged) {
            onMouseDragged(dragged);
        } else if (event instanceof MouseEvent.Clicked clicked) {
            onMouseClicked(clicked);
        } else if (event instanceof MouseEvent.Pressed pressed) {
            onMousePressed(pressed);
        } else if (event instanceof MouseEvent.Released released) {
            onMouseReleased(released);
        } else if (event instanceof MouseEvent.WheelMoved wheelMoved) {
            onMouseWheelMoved(wheelMoved);
        }
    }

    public void onMouseMoved(MouseEvent.Moved moved) {
    }

    public void onMouseDragged(MouseEvent.Dragged dragged) {
    }

    public void onMouseClicked(MouseEvent.Clicked clicked) {
    }

    public void onMousePressed(MouseEvent.Pressed pressed) {
    }

    public void onMouseReleased(MouseEvent.Released released) {
    }

    public void onMouseWheelMoved(MouseEvent.WheelMoved released) {
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
