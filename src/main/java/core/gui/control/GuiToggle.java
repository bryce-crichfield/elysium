package core.gui.control;

import core.graphics.Renderer;
import core.gui.GuiComponent;
import core.gui.container.GuiContainer;
import core.gui.input.GuiEventState;
import core.gui.input.GuiMouseHandler;
import core.gui.layout.GuiHorizontalLayout;
import core.input.Mouse;
import core.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class GuiToggle extends GuiContainer {
    private final GuiLabel label;
    private final GuiComponent toggleArea;

    @Getter
    @Setter
    private boolean isToggled;

    public GuiToggle(String text, int width, int height) {
        super(0, 0, width, height);

        this.setLayout(new GuiHorizontalLayout());

        label = new GuiLabel(100, 30, "Toggle");
        label.setText(text);
        this.addChild(label);

        toggleArea = new GuiComponent(0, 0, 30, 30) {

            @Override
            protected String getComponentName() {
                return "ToggleArea";
            }
            @Override
            protected void onRender(Renderer renderer) {
                // Render the toggle area
                renderer.setColor(Color.RED);
                renderer.fillRect(0, 0, getWidth(), getHeight());

                // Optionally, render the toggle state (on/off)
                if (isToggled) {
                    renderer.setColor(Color.GREEN);
                    renderer.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        toggleArea.addMouseHandler(new GuiMouseHandler() {
            @Override
            public GuiEventState onMouseClicked(MouseEvent.Clicked e) {
                if (e.getButton() == Mouse.LEFT) {
                    isToggled = !isToggled;
                    return GuiEventState.CONSUMED;
                }
                return GuiEventState.NOT_CONSUMED;
            }
        });

        this.addChild(toggleArea);
    }
}
