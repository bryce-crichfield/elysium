package game.platform.texture;

import game.asset.AssetStore;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextureStore implements AssetStore<String, Texture> {
    @Getter
    private static final TextureStore instance = new TextureStore();

    private final Map<String, Texture> textures = new HashMap<>();

    private TextureStore() {
    }

    @Override
    public void store(String key, Texture asset) {
        textures.put(key, asset);
    }

    @Override
    public Texture getAssets(String key) {
        if (!textures.containsKey(key)) {
            throw new IllegalArgumentException("Texture not found: " + key);
        }

        return textures.get(key);
    }

    @Override
    public Set<String> getAssets() {
        return textures.keySet();
    }

    @Override
    public void release() {
        for (var texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
    }
}
