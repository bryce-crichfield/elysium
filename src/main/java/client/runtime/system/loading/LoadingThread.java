package client.runtime.system.loading;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoadingThread extends Thread {
    private int totalStages;
    private final ConcurrentLinkedQueue<LoadingStage> loadingQueue;
    private LoadingStage currentStage;

    public LoadingThread() {
        super("LoadingThread");
        this.loadingQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        System.out.println("LoadingThread: Starting loading process...");

        while (true) {
            // Wait for the queue to have stages
            while (loadingQueue.isEmpty()) {
                currentStage = null; // Current stage is null when waiting

                try {
                    Thread.sleep(50); // Polling interval
                } catch (InterruptedException e) {
                    System.err.println("LoadingThread: Interrupted while waiting for stages.");
                    return; // Exit if interrupted
                }
            }

            currentStage = loadingQueue.poll();

            if (currentStage == null) {
                break;
            }

            try {
                currentStage.loadBlocking();
            } catch (Exception e) {
                System.err.println("LoadingThread: Error during loading stage: " + e.getMessage());
                e.printStackTrace();
            }

            // we have either finished loading or encountered an error, but its not enqueued again
            totalStages--;
        }


        System.out.println("LoadingThread: Loading complete.");
    }


    // return 0 if still loading, 1 if complete
    public float getProgress() {
        if (totalStages <= 0) {
            return 1.0f; // No stages to load, consider it complete
        }

        var remainingStages = (float) loadingQueue.size();
        return 1 - (remainingStages / totalStages);
    }

    public boolean isComplete() {
        return loadingQueue.isEmpty() && (currentStage == null);
    }

    public String getLoadingMessage() {
        if (currentStage != null) {
            return currentStage.getDescription();
        }
        return "Loading complete.";
    }

    public void queueStages(List<LoadingStage> stages) {
        if (stages != null && !stages.isEmpty()) {
            totalStages += stages.size();
            loadingQueue.addAll(stages);
        }
    }
}
