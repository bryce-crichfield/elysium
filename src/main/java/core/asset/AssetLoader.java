//package core.asset;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.HashSet;
//import java.util.Queue;
//import java.util.Set;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public abstract class AssetLoader<K, A> {
//    private final AssetStore<K, A> store;
//    protected final Path path;
//
//    protected final int fileCount;
//    protected final Queue<File> files = new ConcurrentLinkedQueue<>();
//
//    public AssetLoader(AssetStore<K, A> store, Path path) {
//        this.store = store;
//        this.path = path;
//        this.files.addAll(getFiles(path, getExtensions()));
//        this.fileCount = files.size();
//    }
//
//    public abstract Set<String> getExtensions();
//    public abstract boolean load(Runnable callback);
//    protected abstract A loadFile(File file, String key) throws Exception;
//    public final int getAssetCount() {
//        return fileCount;
//    }
//
//    public final float getProgress() {
//        if (fileCount == 0) {
//            return 1f;
//        }
//
//        return (float) (fileCount - files.size()) / fileCount;
//    }
//
//    public static Set<File> getFiles(Path path, Set<String> extensions) {
//        Set<File> result = new HashSet<>();
//
//        try (Stream<Path> pathStream = Files.walk(path)) {
//            result = pathStream
//                    .filter(Files::isRegularFile)
//                    .filter(p -> {
//                        String fileName = p.getFileName().toString();
//                        return extensions.stream()
//                                .anyMatch(ext -> fileName.toLowerCase().endsWith(ext.toLowerCase()));
//                    })
//                    .map(Path::toFile)
//                    .collect(Collectors.toSet());
//        } catch (IOException e) {
//            // Handle exception appropriately for your application
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//}

package core.asset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AssetLoader<K, A> {
    private final String name;
    private final AssetStore<K, A> store;
    protected final Path path;

    protected final int fileCount;
    protected final Queue<File> files = new ConcurrentLinkedQueue<>();

    // Statistics counters
    protected final AtomicInteger successfullyLoaded = new AtomicInteger(0);
    protected final AtomicInteger failedToLoad = new AtomicInteger(0);

    // Loading state
    protected volatile boolean isLoading = false;
    protected volatile boolean loadingComplete = false;

    public AssetLoader(AssetStore<K, A> store, Path path, String name) {
        this.store = store;
        this.path = path;
        this.files.addAll(getFiles(path, getExtensions()));
        this.fileCount = files.size();
        this.name = name;
    }

    public abstract Set<String> getExtensions();

    /**
     * Loads a single file and returns the asset
     * @param file The file to load
     * @param key The key to use for the asset
     * @return The loaded asset
     * @throws Exception If loading fails
     */
    protected abstract A loadFile(File file, K key) throws Exception;

    /**
     * Generates a key for the asset based on the file
     * @param file The file
     * @return The key
     */
    protected abstract K generateKey(File file);

    public boolean load(Runnable onComplete) {
        System.out.println("Starting asset loading from: " + path);
        if (isLoading) {
            System.err.println("Assets are already being loaded");
            return false;
        }

        // Reset statistics
        successfullyLoaded.set(0);
        failedToLoad.set(0);

        // If no assets found
        if (fileCount == 0) {
            System.out.println("No assets found in: " + path);
            return false;
        }

        isLoading = true;
        loadingComplete = false;

        // Start loading in a background thread
        Thread loadingThread = new Thread(() -> {
            try {
                System.out.println("Started loading " + fileCount + " assets");

                File file;
                int processed = 0;

                while ((file = files.poll()) != null) {
                    processed++;
                    K key = generateKey(file);
                    loadSingleAsset(key, file, processed);
                }

                loadingComplete = true;
                isLoading = false;

                System.out.println("Asset loading complete: " +
                        successfullyLoaded.get() + "/" + fileCount + " assets loaded successfully");

                // Run onComplete if provided
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (Exception e) {
                System.err.println("Error during asset loading: " + e.getMessage());
                isLoading = false;
            }
        });

        loadingThread.setName(getClass().getSimpleName() + "-Thread");
        loadingThread.start();

        return true;
    }

    private void loadSingleAsset(K key, File file, int processedCount) {
        try {
            // Add a small delay to prevent overwhelming the system
            Thread.sleep(1);

            // Load the asset using the implementation-specific method
            A asset = loadFile(file, key);

            // Store the asset in the asset store
            store.store(key, asset);
            successfullyLoaded.incrementAndGet();

        } catch (Exception e) {
            failedToLoad.incrementAndGet();
            String errorMsg = "Failed to load: " + key + " - " + e.getMessage();
            System.err.println(errorMsg);
        }
    }

    public final int getAssetCount() {
        return fileCount;
    }

    public final float getProgress() {
        if (fileCount == 0) {
            return 1f;
        }

        return (float) (fileCount - files.size()) / fileCount;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLoadingComplete() {
        return loadingComplete;
    }

    public int getSuccessfullyLoaded() {
        return successfullyLoaded.get();
    }

    public int getFailedToLoad() {
        return failedToLoad.get();
    }

    public String getDebugMessage() {
        return String.format("Loading %s: %d/%d loaded, %d failed",
                name, successfullyLoaded.get(), fileCount, failedToLoad.get());
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
            e.printStackTrace();
        }

        return result;
    }
}