package game.gui.style;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

import java.awt.*;
import java.util.Optional;

@Getter
@With
@AllArgsConstructor
public class GuiStyle {
    private final Color backgroundColor;
    private final Color foregroundColor;
    private final GuiBorder border;
    private final GuiFont font;
    private final GuiBackground background;

    private final Optional<GuiStyle> hoverStyle;
    private final Optional<GuiStyle> focusStyle;
    private final Optional<GuiStyle> disabledStyle;
}
