package game.gui.layout;

import game.gui.GuiElement;

public interface GuiLayout {
    void setAlignment(GuiAlignment alignment);
    void setJustify(GuiJustification justify);
    void setPadding(int padding);
    void setSpacing(int spacing);
    void onLayout(GuiElement parent);
}
