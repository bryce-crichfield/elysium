package game.gui.style;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public class GuiFont {
    private Color color;
    private final String name;
    private final int size;
}
