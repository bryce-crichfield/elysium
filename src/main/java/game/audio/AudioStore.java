package game.audio;

import game.asset.AssetStore;

import javax.sound.sampled.Clip;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AudioStore implements AssetStore<String, Clip> {
    private final Map<String, Clip> loadedClips = new ConcurrentHashMap<>();

    @Override
    public void store(String key, Clip asset) {
        loadedClips.put(key, asset);
    }

    @Override
    public Clip getAssets(String key) {
        return loadedClips.get(key);
    }

    @Override
    public Set<String> getAssets() {
        return loadedClips.keySet();
    }

    @Override
    public void release() {
        for (Clip clip : loadedClips.values()) {
            clip.close();
        }
        loadedClips.clear();
        System.out.println("All audio assets released");
    }
}