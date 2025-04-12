package game.platform;

public interface FontInfo {
    int stringWidth(String text);

    int getAscent();

    int getDescent();

    int getHeight();
}
