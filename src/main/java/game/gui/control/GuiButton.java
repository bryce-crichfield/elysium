package game.gui.control;

import game.graphics.Renderer;
import game.graphics.font.FontInfo;
import game.gui.GuiComponent;
import game.gui.input.GuiEventState;
import game.gui.input.GuiMouseHandler;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class GuiButton extends GuiComponent {
    private String text;
    private Runnable onClickRunnable;
    private GuiBackground background = new GuiBackground.Fill(Color.BLACK);
    private GuiBorder border = new GuiBorder(Color.WHITE, 2);

    public GuiButton(String text, int width, int height, Runnable onClickRunnable) {
        super(0, 0, width, height);
        this.text = text;
        this.onClickRunnable = onClickRunnable;

        this.addMouseHandler(new GuiMouseHandler() {
            @Override
            public GuiEventState onMouseClicked(MouseEvent.Clicked e) {
                onClickRunnable.run();
                return GuiEventState.CONSUMED;
            }
        });
    }

    @Override
    public void onRender(Renderer renderer) {
        // Render button background
        background.render(renderer, getWidth(), getHeight(), 0);
        // Render button border
        border.render(renderer, getWidth(), getHeight(), 0);
        // Render button text
        renderer.setColor(Color.WHITE);
        renderer.setFont(new Font("fonts/arial", Font.BOLD, 8));
        FontInfo fontInfo = renderer.getFontInfo();
        int textWidth = fontInfo.getStringWidth(text);
        int textheight = fontInfo.getHeight();
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() - textheight) / 2;
        renderer.drawString(text, textX, textY);
    }

}
