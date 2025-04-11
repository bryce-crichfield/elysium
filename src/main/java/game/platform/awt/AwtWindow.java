package game.platform.awt;

import game.Game;
import game.input.KeyEvent;
import game.input.Mouse;
import game.input.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Optional;
import game.platform.Window;

public class AwtWindow extends Window {
    private final JFrame frame;
    private final BufferedImage buffer;
    private final Canvas canvas = new Canvas();
    private final BufferStrategy strategy;

    public AwtWindow(int width, int height, Game game) {
        super(width, height, game);
        this.buffer = new BufferedImage(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        canvas.setSize(width, height);
        canvas.setFocusable(true);
        canvas.requestFocus();
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                super.keyTyped(e);
                game.getKeyboard().keyTyped(KeyEvent.fromAwt(e));
            }

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                super.keyPressed(e);
                game.getKeyboard().keyPressed(KeyEvent.fromAwt(e));
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                super.keyReleased(e);
                game.getKeyboard().keyReleased(KeyEvent.fromAwt(e));
            }
        });

        canvas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                var point = transformCoordinates(e.getX(), e.getY());
                if (point.isEmpty()) {
                    return; // Ignore events outside the game area
                }
                var event = new MouseEvent.Pressed(point.get(), e.getButton(), e.getClickCount(), false);
                game.getMouse().mousePressed(event);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                var point = transformCoordinates(e.getX(), e.getY());
                if (point.isEmpty()) {
                    return; // Ignore events outside the game area
                }
                var event = new MouseEvent.Released(point.get(), e.getButton(), false);
                game.getMouse().mouseReleased(event);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                var point = transformCoordinates(e.getX(), e.getY());
                if (point.isEmpty()) {
                    return; // Ignore events outside the game area
                }
                var event = new MouseEvent.Clicked(point.get(), e.getButton(), e.getClickCount(), false);
                game.getMouse().mouseClicked(event);
            }
        });

        canvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                var point = transformCoordinates(e.getX(), e.getY());
                if (point.isEmpty()) {
                    return; // Ignore events outside the game area
                }
                var event = new MouseEvent.Moved(point.get(), false);
                game.getMouse().mouseMoved(event);
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                var point = transformCoordinates(e.getX(), e.getY());
                if (point.isEmpty()) {
                    return; // Ignore events outside the game area
                }
                var event = new MouseEvent.Dragged(point.get(), e.getButton(), false);
                game.getMouse().mouseDragged(event);
            }
        });

        canvas.addMouseWheelListener(
                e -> {
                    Optional<Point> point = transformCoordinates(e.getX(), e.getY());
                    if (point.isPresent()) {
                        var transformedEvent = new MouseEvent.WheelMoved(point.get(), 0, e.getWheelRotation(), false);
                        game.getMouse().mouseWheelMoved(transformedEvent);
                    }
                }
        );

        frame.add(canvas);
        frame.pack();

        canvas.createBufferStrategy(4);
        strategy = canvas.getBufferStrategy();
    }

    @Override
    public boolean isActive() {
        return frame.isActive();
    }

    @Override
    public void onInit() {

    }

    private Optional<Point> transformCoordinates(int canvasX, int canvasY) {
        float canvasAspectRatio = (float) canvas.getWidth() / (float) canvas.getHeight();
        float bufferAspectRatio = (float) buffer.getWidth() / (float) buffer.getHeight();

        int displayWidth;
        int displayHeight;

        if (bufferAspectRatio > canvasAspectRatio) {
            displayWidth = canvas.getWidth();
            displayHeight = (int) (canvas.getWidth() / bufferAspectRatio);
        } else {
            displayHeight = canvas.getHeight();
            displayWidth = (int) (canvas.getHeight() * bufferAspectRatio);
        }

        int centerX = (canvas.getWidth() - displayWidth) / 2;
        int centerY = (canvas.getHeight() - displayHeight) / 2;

        // Check if coordinates are outside the game area (in letterbox space)
        if (canvasX < centerX || canvasX > centerX + displayWidth ||
                canvasY < centerY || canvasY > centerY + displayHeight) {
            // Coordinates are in letterbox space
            return Optional.empty();
        }

        // Convert canvas coordinates to game buffer coordinates
        // Use ratio method to preserve precision
        float xRatio = (canvasX - centerX) / (float) displayWidth;
        float yRatio = (canvasY - centerY) / (float) displayHeight;
        int gameX = (int) (xRatio * buffer.getWidth());
        int gameY = (int) (yRatio * buffer.getHeight());

        // Clamp to game boundaries
        gameX = Math.max(0, Math.min(gameX, buffer.getWidth() - 1));
        gameY = Math.max(0, Math.min(gameY, buffer.getHeight() - 1));

        return Optional.of(new Point(gameX, gameY));
    }

    public void onRender(float updateTime, float renderTime) {
        frame.repaint();

        Graphics2D g = (Graphics2D) buffer.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        game.render(new AwtRenderer(g));

        Graphics2D g2 = (Graphics2D) strategy.getDrawGraphics();
        g2.setColor(Color.BLACK);

        g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        float canvasAspectRatio = (float) canvas.getWidth() / (float) canvas.getHeight();
        float bufferAspectRatio = (float) buffer.getWidth() / (float) buffer.getHeight();

        int displayWidth = 0;
        int displayHeight = 0;

        if (bufferAspectRatio > canvasAspectRatio) {
            displayWidth = canvas.getWidth();
            displayHeight = (int) (canvas.getWidth() / bufferAspectRatio);
        } else {
            displayHeight = canvas.getHeight();
            displayWidth = (int) (canvas.getHeight() * bufferAspectRatio);
        }

        int centerX = (canvas.getWidth() - displayWidth) / 2;
        int centerY = (canvas.getHeight() - displayHeight) / 2;
        g2.drawImage(buffer, centerX, centerY, displayWidth, displayHeight, null);
        drawDebugStats(g2, updateTime, renderTime);
        g2.dispose();

        strategy.show();
    }

    @Override
    public void onClose() {
        frame.dispose();
    }

    private void drawDebugStats(Graphics2D graphics, float updateTime, float renderTime) {
        graphics.setColor(Color.WHITE);

        String upsString = String.format("%.1f ups", 1f / updateTime);
        String fpsString = String.format("%.1f fps", 1f / renderTime);

        String updateTimeString = String.format("%.1f ms", updateTime * 1000);
        String renderTimeString = String.format("%.1f ms", renderTime * 1000);

        final int textSize = 12;
        int y = 20;

        Font font = graphics.getFont();
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(font.getName(), Font.PLAIN, textSize));

        graphics.drawString("UPS: " + upsString, 10, y);
        y += textSize;

        graphics.drawString("FPS: " + fpsString, 10, y);
        y += textSize;

        graphics.drawString("Update Time: " + updateTimeString, 10, y);
        y += textSize;

        graphics.drawString("Render Time: " + renderTimeString, 10, y);
        y += textSize;

        graphics.setFont(font);
    }
}
