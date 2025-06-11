package client.core.gui.control;

import client.core.graphics.Renderer;
import client.core.graphics.font.FontInfo;
import client.core.gui.GuiComponent;
import client.core.gui.input.GuiEventState;
import client.core.gui.input.GuiMouseHandler;
import client.core.gui.style.*;
import client.core.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuiButton extends GuiComponent {
  private String text;
  private Runnable onClickRunnable;

  public GuiButton(String text, int width, int height, Runnable onClickRunnable) {
    super(0, 0, width, height);
    this.text = text;
    this.onClickRunnable = onClickRunnable;

    this.addMouseHandler(
        new GuiMouseHandler() {
          @Override
          public GuiEventState onMouseClicked(MouseEvent.Clicked e) {
            onClickRunnable.run();
            return GuiEventState.CONSUMED;
          }
        });

    style = GuiTheme.getInstance().button();
  }

  @Override
  public void onRender(Renderer renderer) {
    // Render button background
    GuiStyle backgroundStyle = isHovered ? style.getHoverStyle() : style;
    backgroundStyle.getBackground().render(renderer, getWidth(), getHeight(), 0);

    // Render button border
    GuiStyle borderStyle = isHovered ? style.getHoverStyle() : style;
    borderStyle.getBorder().render(renderer, getWidth(), getHeight(), 0);

    // Render button text
    GuiFont font = style.getFont();
    renderer.setColor(font.getColor());
    renderer.setFont(font.getName(), font.getSize());

    FontInfo fontInfo = renderer.getFontInfo();
    int textWidth = fontInfo.getStringWidth(text);
    int textHeight = fontInfo.getHeight();
    int textX = (getWidth() - textWidth) / 2;
    int textY = (getHeight() - textHeight) / 2 + fontInfo.getAscent();

    renderer.drawString(text, textX, textY);
  }

  @Override
  protected String getComponentName() {
    return "button";
  }
}
