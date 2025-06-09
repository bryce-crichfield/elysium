package client.core.gui.style;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import client.core.graphics.Renderer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.awt.*;
import java.util.Optional;

@Getter
@Setter
@With
@RequiredArgsConstructor
public class GuiBorder {
    private final Color color;
    private final int thickness;

    public void render(Renderer renderer, int width, int height, int radius) {
        renderer.setColor(color);
        var oldStroke = renderer.getLineWidth();
        renderer.setLineWidth(thickness);

        if (radius > 0) {
            renderer.drawRoundRect(0, 0, width, height, radius, radius);
        } else {
            renderer.drawRect(0, 0, width, height);
        }

        renderer.setLineWidth(oldStroke);
    }

    public static Optional<GuiBorder> deserialize(JsonObject json) {
        try {
            JsonArray colorArray = json.getAsJsonArray("color");
            Color color = new Color(
                    colorArray.get(0).getAsInt(),
                    colorArray.get(1).getAsInt(),
                    colorArray.get(2).getAsInt(),
                    colorArray.get(3).getAsInt()
            );

            int thickness = json.get("thickness").getAsInt();
            return Optional.of(new GuiBorder(color, thickness));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
