package core.state;

import core.GameContext;
import core.graphics.Renderer;
import core.graphics.background.Background;
import core.graphics.background.BackgroundFactory;
import core.gui.input.GuiEventState;
import core.gui.manager.GuiMouseCaptureManager;
import core.input.MouseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class GameState {
    @Getter
    protected final GameContext gameContext;
    private final List<Background> backgrounds = new ArrayList<>();

    public void addBackground(BackgroundFactory factory) {
        Background background = factory.create(GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT);
        if (background != null) {
            backgrounds.add(background);
        }
    }

    public void onEnter() {
    }

    public final void dispatchMouseEvent(MouseEvent event) {
        if (GuiMouseCaptureManager.getInstance().dispatchToCapturedComponent(event) == GuiEventState.CONSUMED) {
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
        renderer.fillRect(0, 0, GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT);

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
