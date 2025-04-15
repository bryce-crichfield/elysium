package game.gui.style;

import game.graphics.Renderer;
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
        var oldStroke = renderer.getLineWidth();
        renderer.setLineWidth(thickness);

        if (radius > 0) {
            renderer.drawRoundRect(0, 0, width, height, radius, radius);
        } else {
            renderer.drawRect(0, 0, width, height);
        }

        renderer.setLineWidth(oldStroke);
    }
}
