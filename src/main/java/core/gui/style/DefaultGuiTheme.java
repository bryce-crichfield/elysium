package core.gui.style;

import java.awt.*;
import java.util.Optional;

public class DefaultGuiTheme extends GuiTheme {
    @Override
    public GuiStyle button() {
        return GuiStyle.builder()
                .background(new GuiBackground.Fill(Color.BLACK))
                .border(new GuiBorder(Color.WHITE, 2))
                .font(new GuiFont(Color.WHITE, "fonts/neuropol", 12))
                .hoverStyle(Optional.of(buttonHover()))
                .build();
    }

    @Override
    public GuiStyle buttonHover() {
        return GuiStyle.builder()
                .background(new GuiBackground.Fill(Color.GRAY))
                .border(new GuiBorder(Color.RED, 2))
                .font(new GuiFont(Color.WHITE, "fonts/neuropol", 12))
                .build();
    }

    @Override
    public GuiStyle label() {
        return GuiStyle.builder()
                .font(new GuiFont(Color.WHITE, "fonts/neuropol", 12))
                .build();
    }
}
