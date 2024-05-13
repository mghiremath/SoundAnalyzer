package com.adl_detector.sound_analyzer;

import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;

import androidx.core.content.ContextCompat;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
    @Mock private AudioRecord mockAudioRecord;
    @InjectMocks private MainActivity mainActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockAudioRecord.getState()).thenReturn(AudioRecord.STATE_INITIALIZED);
        mainActivity = new MainActivity(); // Assuming dependency injection or manual setting in setup.
    }

    @Test
    public void testStartRecording_PermissionGranted() {
        // Setup context to simulate permission being granted
        when(ContextCompat.checkSelfPermission(any(Context.class), eq(Manifest.permission.RECORD_AUDIO)))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        mainActivity.startRecording();

        verify(mockAudioRecord).startRecording();
        assertTrue("Recording should be started", MainActivity.isRecording);
    }

    @Test
    public void testStartRecording_PermissionDenied() {
        // Setup context to simulate permission being denied
        when(ContextCompat.checkSelfPermission(any(Context.class), eq(Manifest.permission.RECORD_AUDIO)))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        mainActivity.startRecording();

        verify(mockAudioRecord, never()).startRecording();
        assertFalse("Recording should not be started", MainActivity.isRecording);
    }

    @Test
    public void testStopRecording() {
        mainActivity.stopRecording();
        verify(mockAudioRecord).stop();
        assertFalse("Recording should be stopped", MainActivity.isRecording);
    }
}
