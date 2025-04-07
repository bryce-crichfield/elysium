package game.gui.control;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class TextField {
    @Getter
    @Setter
    private String text = "";

    @Getter
    @Setter
    private Color foreground = Color.WHITE;

    @Getter
    @Setter
    private Color background = new Color(50, 50, 50);

    @Getter
    @Setter
    private boolean isEditable = true;

    // Cursor
    private int cursorPosition = 0;
    private boolean cursorVisible = true;
    private Instant lastBlinkTime = Instant.now();
    private final int blinkInterval = 500; // milliseconds

    public TextField(int x, int y, int width, int height) {
    }

    private void insertChar(char c) {
        text = text.substring(0, cursorPosition) + c + text.substring(cursorPosition);
        advanceCursor();
    }

    private void deleteChar() {
        if (cursorPosition > 0) {
            text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
            retreatCursor();
        }
    }

    private void advanceCursor() {
        if (cursorPosition < text.length()) {
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

//        if (!focused || !isEditable) {
//            cursorVisible = false;
//        }
    }

    protected void onRender(Graphics2D g) {

        // Save original clip and color
//        Shape originalClip = setClip(g);
        Color originalColor = g.getColor();

        // Set text color
        g.setColor(foreground);

        // Create clip region to ensure text stays within bounds
//        g.setClip(0, 0, width, height);  // Changed from x, y to 0, 0

        // Get font metrics to properly position text
        FontMetrics metrics = g.getFontMetrics();
        int textHeight = metrics.getHeight();
//        int textY = (height - textHeight) / 2 + metrics.getAscent();  // Remove y +

        // Calculate text offset to keep cursor visible
        int cursorX = 5;  // Changed from x + 5
        int textOffset = 0;

        if (cursorPosition > 0) {
            String textBeforeCursor = text.substring(0, cursorPosition);
            cursorX = 5 + metrics.stringWidth(textBeforeCursor);  // Changed from x + 5

            // If cursor would be outside visible area, adjust text offset
//            if (cursorX > width - 10) {  // Changed from x + width - 10
//                textOffset = (width - 10) - cursorX;  // Changed from (x + width - 10)
//            }
        }

        // Draw the text with offset
//        g.drawString(text, 5 + textOffset, textY);  // Changed from x + 5

        // Draw cursor as a vertical line
//        int visibleCursorX = Math.max(5, Math.min(cursorX + textOffset, width - 5));
        if (cursorVisible) {
            g.setColor(Color.YELLOW);
//            g.drawLine(visibleCursorX, 5, visibleCursorX, height - 5);  // Changed y values
        }

        // Restore original clip and color
//        g.setClip(originalClip);
        g.setColor(originalColor);

        // Draw bounding box for debugging
        g.setColor(Color.RED);
//        g.drawRect(0, 0, width, height);  // Changed from x, y to 0, 0
    }
}
