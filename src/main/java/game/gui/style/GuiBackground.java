package game.gui.style;

import java.awt.*;

public interface GuiBackground {
    void render(Graphics2D g, int width, int height, int radius);

    class Fill implements GuiBackground {
        private final Color color;

        public Fill(Color color) {
            this.color = color;
        }

        @Override
        public void render(Graphics2D g, int width, int height, int radius) {
            g.setColor(color);
            if (radius > 0) {
                g.fillRoundRect(0, 0, width, height, radius, radius);
            } else {
                g.fillRect(0, 0, width, height);
            }
        }
    }
}
