package client.core.scene;

import client.runtime.application.Application;
import client.core.graphics.Renderer;
import client.core.graphics.background.Background;
import client.core.graphics.background.BackgroundFactory;
import client.core.gui.input.GuiEventState;
import client.core.gui.manager.GuiMouseCaptureManager;
import client.core.input.MouseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class ApplicationScene {
    @Getter
    protected final Application application;
    private final List<Background> backgrounds = new ArrayList<>();

    public void addBackground(BackgroundFactory factory) {
        Background background = factory.create(Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);
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
        renderer.fillRect(0, 0, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);

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
