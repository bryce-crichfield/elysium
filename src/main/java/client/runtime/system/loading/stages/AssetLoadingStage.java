package client.runtime.system.loading.stages;

import client.core.asset.AssetLoader;
import client.runtime.system.loading.LoadingStage;
import java.util.concurrent.atomic.AtomicBoolean;

public class AssetLoadingStage implements LoadingStage {
  private final AssetLoader<?, ?> assetLoader;

  public AssetLoadingStage(AssetLoader<?, ?> assetLoader) {
    this.assetLoader = assetLoader;
  }

  @Override
  public String getDescription() {
    return "Loading assets from " + assetLoader.getName() + " _ " + "files)";
  }

  @Override
  public void loadBlocking() {
    AtomicBoolean isLoaded = new AtomicBoolean(false);
    assetLoader.load(
        () -> {
          isLoaded.set(true);
        });

    // Wait for the asset to load
    while (!isLoaded.get()) {
      try {
        Thread.sleep(50); // Polling interval
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Restore interrupted status
        break; // Exit if interrupted
      }
    }
  }
}
