package com.adl_detector.sound_analyzer;
import android.media.AudioRecord;

import java.io.InputStream;

public class AudioRecordInputStream extends InputStream {
    private final AudioRecord audioRecord;
    private final byte[] buffer;
    private final int bufferSize;

    public AudioRecordInputStream(AudioRecord audioRecord, int bufferSize) {
        this.audioRecord = audioRecord;
        this.bufferSize = bufferSize;
        this.buffer = new byte[bufferSize];
    }

    @Override
    public int read() {
        int bytesRead = audioRecord.read(buffer, 0, bufferSize);
        if (bytesRead < 0) {
            return -1; // End of stream
        }
        return buffer[0] & 0xFF; // Returns one byte at a time, not efficient but complies with the base method
    }

    @Override
    public int read(byte[] b, int off, int len) {
        int bytesRead = audioRecord.read(b, off, len);
        if (bytesRead < 0) {
            return -1; // End of stream
        }
        return bytesRead;
    }

    @Override
    public void close() {
        audioRecord.stop();
        audioRecord.release();
    }
}

