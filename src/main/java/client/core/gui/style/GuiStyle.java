package client.core.gui.style;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@With
@AllArgsConstructor
@Builder
public class GuiStyle {
    @Getter
    private final GuiBackground background;
    @Getter
    private final GuiBorder border;
    @Getter
    private final GuiFont font;

    private final Optional<GuiStyle> hoverStyle;
    private final Optional<GuiStyle> focusStyle;
    private final Optional<GuiStyle> disabledStyle;

    public GuiStyle getHoverStyle() {
        return hoverStyle.orElse(this);
    }


    public static Map<String, GuiStyle> load(String styleJson) {
        Map<String, GuiStyle> styles = new HashMap<>();

        // Use try-with-resources to ensure proper resource closing
        try (InputStream fileIn = new FileInputStream(styleJson);
             InputStreamReader reader = new InputStreamReader(fileIn, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String name = entry.getKey();
                try {
                    JsonObject styleJsonObject = entry.getValue().getAsJsonObject();
                    GuiStyle style = GuiStyle.deserialize(styleJsonObject);
                    styles.put(name, style);
                    System.out.println("Loaded style: " + name);
                } catch (Exception e) {
                    System.err.println("Failed to parse style '" + name + "': " + e.getMessage());
                    // Continue processing other styles instead of failing completely
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to read style file: " + e.getMessage());
        } catch (JsonParseException e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading styles: " + e.getMessage());
            e.printStackTrace();
        }

        return styles;
    }

    public static GuiStyle deserialize(com.google.gson.JsonObject json) {
        var background = GuiBackground.deserialize(json.getAsJsonObject("background"))
                .orElse(new GuiBackground.Fill(Color.BLACK));
        var border = GuiBorder.deserialize(json.getAsJsonObject("border"))
                .orElse(new GuiBorder(Color.BLACK, 0));
        var font = GuiFont.deserialize(json.getAsJsonObject("font"))
                .orElse(new GuiFont(Color.WHITE, "/fonts/arial", 12));

        var hoverStyle = Optional.ofNullable(json.getAsJsonObject("hover"))
                .map(GuiStyle::deserialize);
        var focusStyle = Optional.ofNullable(json.getAsJsonObject("focus"))
                .map(GuiStyle::deserialize);
        var disabledStyle = Optional.ofNullable(json.getAsJsonObject("disabled"))
                .map(GuiStyle::deserialize);

        return new GuiStyle(background, border, font, hoverStyle, focusStyle, disabledStyle);
    }
}
