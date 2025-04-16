//package game.gui.control;
//
//import game.gui.style.GuiBorder;
//
//import java.util.Optional;
//
//
//import java.awt.*;
//
//public class Button extends GuiComponent {
//    private String text;
//    private Font font = new Font("arial", Font.PLAIN, 14);
//    private Color textColor = Color.WHITE;
//    private Color hoverColor = new Color(255, 0, 0);
//    private Color normalColor = new Color(70, 70, 70);
//
//    public Button(int x, int y, int width, int height, String text) {
//        super(x, y, width, height);
//        this.text = text;
//        this.backgroundColor = normalColor;
//        this.cornerRadius = 5;
//        this.border = Optional.of(new GuiBorder(new Color(30, 30, 30), 1));
//    }
//
//    @Override
//    protected void onMouseEnter() {
//        backgroundColor = hoverColor;
//    }
//
//    @Override
//    protected void onMouseExit() {
//        backgroundColor = normalColor;
//    }
//
//    @Override
//    protected void onSpriteRender(Graphics2D g) {
//        // Save the original clip
////        Shape originalClip = g.getClip();
//        Shape originalClip = setClip(g);
//        // Set clipping to button boundaries
////        g.setClip(0, 0, width, height);
//
//        // Draw text centered in the button
//        g.setFont(font);
//        FontMetrics metrics = g.getFontInfo(font);
//        int textX = (width - metrics.getStringWidth(text)) / 2;
//        int textY = ((height - metrics.getHeight()) / 2) + metrics.getAscent();
//
//        g.setColor(textColor);
//        g.drawString(text, textX, textY);
//
//        // Restore the original clip
//        g.setClip(originalClip);
//    }
//
//    // Fluent setters
//    public Button setText(String text) {
//        this.text = text;
//        return this;
//    }
//
//    public Button setTextColor(Color color) {
//        this.textColor = color;
//        return this;
//    }
//
//    public Button setFont(Font font) {
//        this.font = font;
//        return this;
//    }
//
//    @Override
//    public void onClick() {
//        super.onClick();
//        if (onClick != null) {
//            onClick.run();
//        }
//    }
//
//    public Button setOnClick(Runnable handler) {
//        this.onClick = handler;
//        return this;
//    }
//
//}