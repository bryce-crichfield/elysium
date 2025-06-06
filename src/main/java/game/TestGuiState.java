package game;

import core.state.GameState;
import core.GameContext;
import core.graphics.Renderer;
import core.gui.container.GuiContainer;
import core.gui.container.GuiScrollPanel;
import core.gui.control.*;
import core.gui.layout.GuiVerticalLayout;
import core.gui.style.GuiBorder;
import core.gui.style.GuiTheme;
import core.input.MouseEvent;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class TestGuiState extends GameState {
    GuiContainer gui = new GuiContainer(0, 0, GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT);
    public TestGuiState(GameContext gameContext) {
        super(gameContext);

        var spinner = new GuiSpinner<String>(100, 100, 300, 50);
        spinner.setValues(List.of(
                "Option 1",
                "Option 2",
                "Option 3",
                "Option 4",
                "Option 5"
        ));
        gui.addChild(spinner);

        var toggle = new GuiToggle("Toggle", 200, 50);
        toggle.setPosition(400, 100);
        var toggleStyle = GuiTheme.getInstance().button();
        toggleStyle = toggleStyle.withBorder(new GuiBorder(Color.WHITE, 2));
        toggle.setStyle(toggleStyle);
//        gui.addChild(toggle);


        var testContainer = new GuiContainer(600, 100, 200, 200);
        testContainer.setLayout(new GuiVerticalLayout());
        testContainer.setStyle(GuiTheme.getInstance().button());

        var slider = new GuiSlider(200, 50);
        slider.setPosition(0, 0);
        testContainer.addChild(slider);

        GuiDropdown<String> dropdown = new GuiDropdown<>(200, 200, 200, 25);
        dropdown.setItems(List.of(
                "Option 1",
                "Option 2",
                "Option 3",
                "Option 4"
        ));
        dropdown.setSelectedItem("Option 2");
        gui.addChild(dropdown);

        GuiButton button = new GuiButton("Click Me", 200, 50, () -> {});
        button.setPosition(200, 275);
        gui.addChild(button);


        GuiScrollPanel scroll = new GuiScrollPanel(300, 300, 200, 200);
        scroll.setLayout(new GuiVerticalLayout());
        for (int i = 0; i < 20; i++) {
            GuiButton btn = new GuiButton("Button " + (i + 1), 200, 50, () -> {});
            btn.setStyle(GuiTheme.getInstance().button());
            scroll.addChild(btn);
        }
        gui.addChild(scroll);
//        gui.addChild(testContainer);
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
