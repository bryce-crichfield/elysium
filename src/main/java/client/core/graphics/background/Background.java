package client.core.graphics.background;

import client.core.graphics.Renderer;
import java.time.Duration;

public abstract class Background {
  protected int screenWidth;
  protected int screenHeight;

  public Background(int screenWidth, int screenHeight) {
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
  }

  public static BackgroundFactory stars() {
    return (screenWidth, screenHeight) -> {
      var vertexPath = "shaders/stars/StarsVertex.glsl";
      var fragmentPath = "shaders/stars/StarsFragment.glsl";
      return new ShaderBackground(vertexPath, fragmentPath, screenWidth, screenHeight);
    };
  }

  public abstract void update(Duration delta);

  public abstract void render(Renderer renderer);
}
