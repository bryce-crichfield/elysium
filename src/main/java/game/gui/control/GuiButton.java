package game.gui.control;

import game.gui.GuiElement;
import game.gui.input.GuiMouseHandler;
import game.gui.interfaces.GuiRenderable;
import lombok.Setter;

import java.awt.*;

public class GuiButton extends GuiElement {
    @Setter
    private String text;
    private GuiRenderable textRenderable;

    public GuiButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.text = "";
        this.createText();
        this.guiRenderHandler = textRenderable;
    }

    private void createText() {
        textRenderable = graphics -> {
            // Draw the text centered in the button
            FontMetrics metrics = graphics.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(text)) / 2;
            int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            graphics.setColor(Color.WHITE);
            graphics.drawString(text, textX, textY);
        };
    }

    public void onClick(Runnable action) {
        this.setGuiMouseHandler(GuiMouseHandler.onClick(action));
    }
}
