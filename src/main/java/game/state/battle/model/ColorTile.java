package game.state.battle.model;

import game.platform.Renderer;

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
        renderer.setColor(color);
        Composite oldComposite = renderer.getComposite();
        renderer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
        renderer.fillRect(getX() * size, getY() * size, size, size);
        renderer.setComposite(oldComposite);
        renderer.setColor(color.darker().darker());
        renderer.drawRect(getX() * size, getY() * size, size, size);

    }
}
