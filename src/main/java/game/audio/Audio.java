package game.audio;

import javax.sound.sampled.Clip;

public class Audio {
    private final AudioStore store = new AudioStore();

    public Audio() {
    }

    public void play(String name) {
        Clip clip = store.getAssets(name);
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    public void loopPlayForever(String name, float volume) {
        Clip clip = store.getAssets(name);

        // set volume
        float gain = volume;
        if (gain < 0f || gain > 1f) {
            throw new IllegalArgumentException("Volume not valid: " + gain);
        }

        // set volume
        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    public AudioStore getAudioStore() {
        return store;
    }
}
