package core.asset;

import java.util.Set;

public interface AssetStore<K, A> {
    void store(K key, A asset);
    A getAssets(K key);
    Set<K> getAssets();
    void release();
}
