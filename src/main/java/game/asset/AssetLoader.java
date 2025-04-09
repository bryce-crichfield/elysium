package game.asset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AssetLoader<K, A> {
    private final AssetStore<K, A> store;
    protected final Path path;

    protected final int fileCount;
    protected final Queue<File> files = new ConcurrentLinkedQueue<>();

    public AssetLoader(AssetStore<K, A> store, Path path) {
        this.store = store;
        this.path = path;
        this.files.addAll(getFiles(path, getExtensions()));
        this.fileCount = files.size();
    }

    public abstract Set<String> getExtensions();
    public abstract boolean load(Runnable callback);

    public final int getAssetCount() {
        return fileCount;
    }

    public final float getProgress() {
        if (fileCount == 0) {
            return 1f;
        }

        return (float) (fileCount - files.size()) / fileCount;
    }

    public static Set<File> getFiles(Path path, Set<String> extensions) {
        Set<File> result = new HashSet<>();

        try (Stream<Path> pathStream = Files.walk(path)) {
            result = pathStream
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String fileName = p.getFileName().toString();
                        return extensions.stream()
                                .anyMatch(ext -> fileName.toLowerCase().endsWith(ext.toLowerCase()));
                    })
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            // Handle exception appropriately for your application
            e.printStackTrace();
        }

        return result;
    }
}
