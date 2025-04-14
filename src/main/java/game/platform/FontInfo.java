package game.platform;

public interface FontInfo {
    int getLeading();

    int stringWidth(String text);

    int getAscent();

    int getDescent();

    int getHeight();
}
