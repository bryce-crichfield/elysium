package game.audio;

import game.asset.AssetStore;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AudioStore implements AssetStore<String, AudioSample> {
    private final Map<String, AudioSample> loadedClips = new ConcurrentHashMap<>();

    @Override
    public void store(String key, AudioSample asset) {
        loadedClips.put(key, asset);
    }

    @Override
    public AudioSample getAssets(String key) {
        return loadedClips.get(key);
    }

    @Override
    public Set<String> getAssets() {
        return loadedClips.keySet();
    }

    @Override
    public void release() {
        loadedClips.clear();
        System.out.println("All audio assets released");
    }
}