package game.gui.style;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.awt.*;
import java.util.Optional;

@With
@AllArgsConstructor
@Builder
public class GuiStyle {
    @Getter
    private final GuiBackground background;
    @Getter
    private final GuiBorder border;
    @Getter
    private final GuiFont font;

    private final Optional<GuiStyle> hoverStyle;
    private final Optional<GuiStyle> focusStyle;
    private final Optional<GuiStyle> disabledStyle;

    public GuiStyle getHoverStyle() {
        return hoverStyle.orElse(this);
    }
}
