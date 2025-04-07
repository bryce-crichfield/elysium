package game.gui.control;

import game.gui.GuiComponent;
import game.gui.style.GuiBackground;
import game.gui.style.GuiLabel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class TextField extends GuiComponent {
    private GuiLabel text;
    private GuiBackground background;

    @Getter
    @Setter
    private boolean isEditable = true;

    // Cursor
    private int cursorPosition = 0;
    private boolean cursorVisible = true;
    private Instant lastBlinkTime = Instant.now();
    private final int blinkInterval = 500; // milliseconds

    public TextField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    private void insertChar(char c) {
//        text.insert(c, cursorPosition);
        advanceCursor();
    }

    private void deleteChar() {
        if (cursorPosition > 0) {
//            text.remove(cursorPosition - 1);
            retreatCursor();
        }
    }

    private void advanceCursor() {
        if (cursorPosition < text.getText().length()) {
            cursorPosition++;
        }
    }

    private void retreatCursor() {
        if (cursorPosition > 0) {
            cursorPosition--;
        }
    }

    public void onKeyPress(int keyCode) {
        if (!isEditable) return;
        // Handle special keys first
        if (keyCode == 8) { // Backspace
            deleteChar();
        } else if (keyCode == 37) { // Left arrow
            retreatCursor();
        } else if (keyCode == 39) { // Right arrow
            advanceCursor();
        } else if (keyCode >= 32 && keyCode <= 126) { // Printable characters
            insertChar((char) keyCode);
        }
    }

    protected void onUpdate(Duration delta) {

        // Blink cursor every blinkInterval milliseconds
        if (Duration.between(lastBlinkTime, Instant.now()).toMillis() > blinkInterval) {
            cursorVisible = !cursorVisible;
            lastBlinkTime = Instant.now();
        }

//        if (!isFocused || !isEditable) {
        if (!isEditable) {
            cursorVisible = false;
        }
    }

    protected void onRender(Graphics2D g) {
        // Save original clip and color
        Shape originalClip = g.getClip();
        Color originalColor = g.getColor();

        // Set text color
        g.setColor(text.getColor());

        // Create clip region to ensure text stays within bounds
        g.setClip(0, 0, width, height);  // Changed from x, y to 0, 0

        // Get font metrics to properly position text
        FontMetrics metrics = g.getFontMetrics();
        int textHeight = metrics.getHeight();
        int textY = (height - textHeight) / 2 + metrics.getAscent();  // Remove y +

        // Calculate text offset to keep cursor visible
        int cursorX = 5;  // Changed from x + 5
        int textOffset = 0;

        if (cursorPosition > 0) {
            String textBeforeCursor = text.getText().substring(0, cursorPosition);
            cursorX = 5 + metrics.stringWidth(textBeforeCursor);  // Changed from x + 5

            // If cursor would be outside visible area, adjust text offset
            if (cursorX > width - 10) {  // Changed from x + width - 10
                textOffset = (width - 10) - cursorX;  // Changed from (x + width - 10)
            }
        }

        // Draw the text with offset
        g.drawString(text.getText(), 5 + textOffset, textY);  // Changed from x + 5

        // Draw cursor as a vertical line
        int visibleCursorX = Math.max(5, Math.min(cursorX + textOffset, width - 5));
        if (cursorVisible) {
            g.setColor(Color.YELLOW);
            g.drawLine(visibleCursorX, 5, visibleCursorX, height - 5);  // Changed y values
        }

        // Restore original clip and color
        g.setClip(originalClip);
        g.setColor(originalColor);

        // Draw bounding box for debugging
        g.setColor(Color.RED);
        g.drawRect(0, 0, width, height);  // Changed from x, y to 0, 0
    }
}
