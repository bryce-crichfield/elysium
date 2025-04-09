package game.audio.automation;

public interface AudioAutomation {
    float getValue(float timeInSeconds);
    boolean isComplete(float timeInSeconds);
}