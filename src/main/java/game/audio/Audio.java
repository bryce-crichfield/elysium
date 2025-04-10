package game.audio;

import game.audio.processor.AudioSampler;

import javax.sound.sampled.AudioFormat;

public class Audio {
    private final AudioStore store = new AudioStore();
    private final AudioEngine engine = new AudioEngine();

    public AudioFormat getFormat() {
        return engine.getFormat();
    }

    public Audio() {
        engine.start();
    }

    public void play(String name) {
        play(name, false, 1.0f);
    }

    public void play(String name, boolean loop, float gain) {
        AudioSample sample = store.getAssets(name);
        if (sample == null) {
            System.err.println("Audio not found: " + name);
            return;
        }

        var sampler = new AudioSampler(sample, engine.getFormat());
        sampler.setLoop(loop);
        sampler.getGain().setValue(gain);
        engine.addProcessor(sampler);
        sampler.play();
    }

    public AudioStore getAudioStore() {
        return store;
    }
}
