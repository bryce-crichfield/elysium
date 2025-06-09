package client.runtime.system.loading;

public interface LoadingStage {
    String getDescription(); // returns a description of the loading stage
    // this should/can block the calling thread until loading is complete
    void loadBlocking() throws Exception;
}
