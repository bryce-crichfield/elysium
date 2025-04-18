package game.gui.style;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import game.graphics.Renderer;

import java.awt.*;
import java.util.Optional;

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


    static Optional<GuiBackground> deserialize(JsonObject json) {
        try {
            String type = json.get("type").getAsString();
            switch (type) {
                case "fill" -> {
                    JsonArray colorArray = json.getAsJsonArray("color");
                    Color color = new Color(
                            colorArray.get(0).getAsInt(),
                            colorArray.get(1).getAsInt(),
                            colorArray.get(2).getAsInt(),
                            colorArray.get(3).getAsInt()
                    );
                    return Optional.of(new Fill(color));
                }
                default -> {
                    throw new IllegalArgumentException("Unknown background type: " + type);
                }
            }}
        catch (Exception e) {
            return Optional.empty();
        }
    }
}
