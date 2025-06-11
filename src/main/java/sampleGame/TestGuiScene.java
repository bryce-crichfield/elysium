package sampleGame;

import client.core.graphics.Renderer;
import client.core.gui.container.GuiContainer;
import client.core.gui.container.GuiScrollPanel;
import client.core.gui.control.*;
import client.core.gui.layout.GuiVerticalLayout;
import client.core.gui.style.GuiBorder;
import client.core.gui.style.GuiTheme;
import client.core.input.MouseEvent;
import client.core.scene.ApplicationScene;
import client.runtime.application.Application;
import java.awt.*;
import java.time.Duration;
import java.util.List;

public class TestGuiScene extends ApplicationScene {
  GuiContainer gui = new GuiContainer(0, 0, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);

  public TestGuiScene(Application game) {
    super(game);

    var spinner = new GuiSpinner<String>(100, 100, 300, 50);
    spinner.setValues(List.of("Option 1", "Option 2", "Option 3", "Option 4", "Option 5"));
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
    dropdown.setItems(List.of("Option 1", "Option 2", "Option 3", "Option 4"));
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
