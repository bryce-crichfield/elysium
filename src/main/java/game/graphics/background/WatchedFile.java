package game.graphics.background;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

/**
 * A simple class to monitor a file for changes
 */
public class WatchedFile {
    @Getter
    private final Path path;

    @Getter
    private FileTime lastModifiedTime;

    public WatchedFile(String path) {
        this.path = Paths.get("resources/"+path).toAbsolutePath();
    }

    public boolean hasChanged() {
        try {
            if (lastModifiedTime == null) {
                lastModifiedTime = Files.getLastModifiedTime(path);
            }

            // Get current modification time
            FileTime currentModTime = Files.getLastModifiedTime(path);

            // Check if it's different from last recorded time
            if (currentModTime.compareTo(lastModifiedTime) != 0) {
                // Update the last modified time
                this.lastModifiedTime = currentModTime;
                return true;
            }

            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public Path getAbsolutePath() {
        return path.toAbsolutePath();
    }
}
