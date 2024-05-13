package com.adl_detector.sound_analyzer;

public class SoundPreProcessor {

    void removeSilence(byte[] audioData) {
        short silenceThreshold = 1000;  // Define what you consider as silence
        for (int i = 0; i < audioData.length; i += 2) {
            short sample = (short) ((audioData[i + 1] << 8) | (audioData[i] & 0xFF));
            if (Math.abs(sample) < silenceThreshold) {
                audioData[i] = 0;
                audioData[i + 1] = 0;
            }
        }
    }


    void applyLowPassFilter(byte[] audioData) {
        float[] filteredSamples = new float[audioData.length / 2];
        float alpha = 0.1f;  // Filter coefficient
        filteredSamples[0] = audioData[0];  // Initial condition

        for (int i = 2; i < audioData.length; i += 2) {
            short sample = (short) ((audioData[i + 1] << 8) | (audioData[i] & 0xFF));
            filteredSamples[i / 2] = filteredSamples[(i / 2) - 1] + alpha * (sample - filteredSamples[(i / 2) - 1]);
            // Convert float back to bytes
            short outputSample = (short) (filteredSamples[i / 2]);
            audioData[i] = (byte) (outputSample & 0xFF);
            audioData[i + 1] = (byte) ((outputSample >> 8) & 0xFF);
        }
    }
    void normalizeAudio(byte[] audioData) {

        short max = findMax(audioData);
        for (int i = 0; i < audioData.length; i += 2) {
            // Convert bytes to a short
            short sample = (short) ((audioData[i + 1] << 8) | (audioData[i] & 0xFF));
            sample = (short) ((sample / (float) max) * Short.MAX_VALUE);
            audioData[i] = (byte) (sample & 0xFF);
            audioData[i + 1] = (byte) ((sample >> 8) & 0xFF);
        }
    }

    private short findMax(byte[] audioData) {
        short max = 0;
        for (int i = 0; i < audioData.length; i += 2) {
            short sample = (short) ((audioData[i + 1] << 8) | (audioData[i] & 0xFF));
            if (Math.abs(sample) > max) {
                max = (short) Math.abs(sample);
            }
        }
        return max;
    }
}
