package client.core.gui.style;

import lombok.Getter;

public abstract class GuiTheme {
  @Getter private static final GuiTheme instance = new DefaultGuiTheme();

  public abstract GuiStyle button();

  public abstract GuiStyle buttonHover();

  public abstract GuiStyle label();
}
