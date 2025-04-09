package game.audio.automation;

public class LinearAudioAutomation implements AudioAutomation {
    private float startValue;
    private float endValue;
    private float duration;  // in seconds

    public LinearAudioAutomation(float startValue, float endValue, float duration) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.duration = duration;
    }

    @Override
    public float getValue(float timeInSeconds) {
        if (timeInSeconds >= duration) {
            return endValue;
        }

        float ratio = timeInSeconds / duration;
        return startValue + ratio * (endValue - startValue);
    }

    @Override
    public boolean isComplete(float timeInSeconds) {
        return timeInSeconds >= duration;
    }
}
