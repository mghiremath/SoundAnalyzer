package com.adl_detector.sound_analyzer;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class SilenceRemovalProcessor implements AudioProcessor {

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioBuffer = audioEvent.getFloatBuffer();
        for (int i = 0; i < audioBuffer.length; i++) {
            // Threshold below which audio is considered silence
            float silenceThreshold = 0.02f;
            if (Math.abs(audioBuffer[i]) < silenceThreshold) {
                audioBuffer[i] = 0;
            }
        }
        return true;
    }

    @Override
    public void processingFinished() {
        // No cleanup needed
    }
}
