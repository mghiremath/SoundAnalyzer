package com.adl_detector.sound_analyzer;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class LowPassFilterProcessor implements AudioProcessor {

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioBuffer = audioEvent.getFloatBuffer();
        float previousValue = audioBuffer[0]; // Start with the first element
        for (int i = 1; i < audioBuffer.length; i++) {
            // Filter coefficient, determines the smoothness
            float alpha = 0.1f;
            audioBuffer[i] = previousValue + alpha * (audioBuffer[i] - previousValue);
            previousValue = audioBuffer[i];
        }
        return true;
    }

    @Override
    public void processingFinished() {
        // No cleanup needed
    }
}
