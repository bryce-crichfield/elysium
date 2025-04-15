package game.gui.style;

import game.graphics.Renderer;

import java.awt.*;

public interface GuiBackground {
    void render(Renderer renderer, int width, int height, int radius);

    class Fill implements GuiBackground {
        private final Color color;

        public Fill(Color color) {
            this.color = color;
        }

        @Override
        public void render(Renderer renderer, int width, int height, int radius) {
            renderer.setColor(color);
            if (radius > 0) {
                renderer.fillRoundRect(0, 0, width, height, radius, radius);
            } else {
                renderer.fillRect(0, 0, width, height);
            }
        }
    }
}
