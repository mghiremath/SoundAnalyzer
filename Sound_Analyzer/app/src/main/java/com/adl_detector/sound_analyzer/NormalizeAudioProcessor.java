package com.adl_detector.sound_analyzer;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class NormalizeAudioProcessor implements AudioProcessor {
    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioBuffer = audioEvent.getFloatBuffer();
        float max = findMax(audioBuffer);
        if (max > 0) {
            for (int i = 0; i < audioBuffer.length; i++) {
                audioBuffer[i] /= max;
            }
        }
        return true;
    }

    @Override
    public void processingFinished() {
        // No cleanup needed
    }

    private float findMax(float[] audioBuffer) {
        float max = 0;
        for (float sample : audioBuffer) {
            if (Math.abs(sample) > max) {
                max = Math.abs(sample);
            }
        }
        return max;
    }
}

