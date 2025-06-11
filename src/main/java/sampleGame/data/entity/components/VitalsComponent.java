package sampleGame.data.entity.components;

import client.core.graphics.Renderer;
import com.google.gson.JsonObject;
import java.awt.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.component.Component;
import sampleGame.data.entity.component.RenderableComponent;

@RequiredArgsConstructor
@AllArgsConstructor
public class VitalsComponent implements RenderableComponent {
  public int movementPoints = 10;
  public int actionPoints = 10;
  public int health = 10;

  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("movementPoints", movementPoints);
    json.addProperty("actionPoints", actionPoints);
    json.addProperty("health", health);
    return json;
  }

  @Override
  public void onVectorRender(Entity self, Renderer renderer) {
    if (self.lacksComponent(CharacterComponent.class)) return;
    if (self.lacksComponent(PositionComponent.class)) return;

    var character = self.getComponent(CharacterComponent.class);
    var position = self.getComponent(PositionComponent.class);

    float healthPercentage = 1.0f;
    Color healthColor =
        healthPercentage > 0.5 ? Color.GREEN : healthPercentage > 0.25 ? Color.YELLOW : Color.RED;
    renderer.setColor(healthColor);
    int healthWidth = (int) ((32 - 10) * healthPercentage);
    int healthHeight = 5;
    int healthX = (int) ((position.getX() * 32) + 5);
    int healthY = (int) ((position.getY() * 32) + 32 - 5);

    renderer.setColor(Color.BLACK);
    renderer.fillRect(healthX, healthY, 32 - 10, healthHeight);
    renderer.setColor(healthColor);
    renderer.fillRect(healthX, healthY, healthWidth, healthHeight);
    renderer.setColor(Color.BLACK);
    renderer.drawRect(healthX, healthY, 32 - 10, healthHeight);
  }

  @Override
  public Component clone() {
    return new VitalsComponent(movementPoints, actionPoints, health);
  }
}
