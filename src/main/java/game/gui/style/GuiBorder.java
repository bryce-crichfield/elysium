package game.gui.style;

import game.platform.Renderer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.awt.*;

@Getter
@Setter
@With
@RequiredArgsConstructor
public class GuiBorder {
    private final Color color;
    private final int thickness;

    public void render(Renderer renderer, int width, int height, int radius) {
        renderer.setColor(color);
        var oldStroke = renderer.getStroke();
        renderer.setStroke(new BasicStroke(thickness));

        if (radius > 0) {
            renderer.drawRoundRect(0, 0, width, height, radius, radius);
        } else {
            renderer.drawRect(0, 0, width, height);
        }

        renderer.setStroke(oldStroke);
    }
}
