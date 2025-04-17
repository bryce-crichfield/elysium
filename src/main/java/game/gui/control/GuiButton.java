package game.gui.control;

import game.graphics.Renderer;
import game.graphics.font.FontInfo;
import game.gui.GuiComponent;
import game.gui.input.GuiEventState;
import game.gui.input.GuiHoverHandler;
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
    private boolean isHovered = false;

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

        this.addHoverHandler(new GuiHoverHandler() {
            @Override
            public void onEnter(MouseEvent event) {
                isHovered = true;
            }

            @Override
            public void onExit(MouseEvent event) {
                isHovered = false;
            }
        });
    }

    @Override
    public void onRender(Renderer renderer) {
        // Render button background
        background.render(renderer, getWidth(), getHeight(), 0);
        // Render button border
        if (isHovered) {
            border = border.withColor(Color.RED);
        } else {
            border = border.withColor(Color.WHITE);
        }
        border.render(renderer, getWidth(), getHeight(), 0);
        // Render button text
        renderer.setColor(Color.WHITE);
        renderer.setFont(new Font("fonts/neuropol", Font.BOLD, 12));
        FontInfo fontInfo = renderer.getFontInfo();
        int textWidth = fontInfo.getStringWidth(text);
        int textHeight = fontInfo.getHeight();
        int textX = (getWidth() - textWidth) / 2;

        // The correct vertical centering calculation
        int textY = (getHeight() - textHeight) / 2 + fontInfo.getAscent();

        renderer.drawString(text, textX, textY);
    }

}
