package client.core.audio.automation;

public interface AudioAutomation {
    float getValue(float timeInSeconds);
    boolean isComplete(float timeInSeconds);
}