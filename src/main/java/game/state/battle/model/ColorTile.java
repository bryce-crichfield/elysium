package game.state.battle.model;

import game.graphics.Renderer;

import java.awt.*;

public class ColorTile extends Tile {
    private final Color color;
    private final int size;

    public ColorTile(int x, int y, boolean passable, Color color) {
        super(x, y, passable);
        this.color = color;
        size = 32;
    }

    @Override
    public void onRender(Renderer renderer) {
        // apply an alpha to the provided color
        int alpha = (int) (255 * 0.15f);
        var drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        renderer.setColor(drawColor);
        renderer.fillRect(getX() * size, getY() * size, size, size);
        renderer.setColor(color.darker().darker());
        renderer.drawRect(getX() * size, getY() * size, size, size);

    }
}
