package client.core.gui.layout;

import client.core.gui.container.GuiContainer;

public interface GuiLayout {
    void setAlignment(GuiAlignment alignment);

    void setJustify(GuiJustification justify);

    void setPadding(int padding);

    void setSpacing(int spacing);

    void onLayout(GuiContainer parent);

}
