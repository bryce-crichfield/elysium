package game.gui.style;

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

    public void render(Graphics2D g, int width, int height, int radius) {
        g.setColor(color);
        var oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness));

        if (radius > 0) {
            g.drawRoundRect(0, 0, width, height, radius, radius);
        } else {
            g.drawRect(0, 0, width, height);
        }

        g.setStroke(oldStroke);
    }
}
