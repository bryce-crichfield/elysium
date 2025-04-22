package game.state;

import game.Game;
import game.graphics.Renderer;
import game.gui.container.GuiContainer;
import game.gui.control.GuiSpinner;
import game.input.MouseEvent;

import java.time.Duration;
import java.util.List;

public class TestGuiState extends GameState {
    GuiContainer gui = new GuiContainer(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
    public TestGuiState(Game game) {
        super(game);

        var spinner = new GuiSpinner<String>(100, 100, 300, 50);
        spinner.setValues(List.of(
                "Option 1",
                "Option 2",
                "Option 3",
                "Option 4",
                "Option 5"
        ));
        gui.addChild(spinner);

        var toggle = new game.gui.control.GuiToggle("Toggle", 200, 50);
        toggle.setPosition(400, 100);
        gui.addChild(toggle);

    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        super.onMouseEvent(event);
        gui.processMouseEvent(event);
    }

    @Override
    public void onUpdate(Duration delta) {
        gui.update(delta);
    }

    @Override
    public void onRender(Renderer renderer) {
        gui.render(renderer);
    }
}
