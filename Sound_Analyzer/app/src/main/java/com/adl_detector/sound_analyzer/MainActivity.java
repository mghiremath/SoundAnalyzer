package com.adl_detector.sound_analyzer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.mfcc.MFCC;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.adl_detector.sound_analyzer.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

//new imports
import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements FirstFragment.RecordingController {
    private final static Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AudioRecord audioRecord;
    public static boolean isRecording = false; // Variable to manage recording state
    private final int sampleRate = 44100;
    private final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private Thread recordingThread = null;
    private static final int RECORD_REQUEST_CODE = 101;
    private AudioDispatcher dispatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setupNavigation();
        setupFab();
    }
    private void setupNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }
    private void setupFab() {
        binding.fab.setOnClickListener(view -> {
            if (!isRecording) {
                startRecording();
                Snackbar.make(view, "Recording started", Snackbar.LENGTH_LONG)
                        .setAction("Stop", v -> stopRecording()).show();
            } else {
                stopRecording();
                Snackbar.make(view, "Recording stopped", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    public void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_REQUEST_CODE);
        } else {
            // Initialize and start AudioRecord
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize);
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording();
                isRecording = true;
                startAudioProcessingThread();
            } else {
                LOGGER.severe("AudioRecord initialization failed.");
                Snackbar.make(binding.getRoot(), "Failed to initialize recording. Check device audio capabilities.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void startAudioProcessingThread() {
        try {
            AudioRecordInputStream arInputStream = new AudioRecordInputStream(audioRecord, AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));
            TarsosDSPAudioFormat torsosFormat = new TarsosDSPAudioFormat(sampleRate, 16, 1, true, false);
            UniversalAudioInputStream audioStream = new UniversalAudioInputStream(arInputStream, torsosFormat);
            dispatcher = new AudioDispatcher(audioStream, 1024, 0);
            setupAudioProcessors();
            recordingThread = new Thread(dispatcher, "Audio Processing Thread");
            recordingThread.start();
        } catch (Exception e) {
            LOGGER.severe("Error starting audio processing thread: " + e.getMessage());
        }
    }

    private void setupAudioProcessors() {
        // Setup MFCC and any other processors
        dispatcher.addAudioProcessor(new NormalizeAudioProcessor());
        dispatcher.addAudioProcessor(new LowPassFilterProcessor());
        dispatcher.addAudioProcessor(new SilenceRemovalProcessor());

        MFCC mfccProcessor = new MFCC(1024, sampleRate, 20, 50, 300, 3000);
        dispatcher.addAudioProcessor(mfccProcessor);
        dispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] mfccCoefficients = mfccProcessor.getMFCC();
                processMFCCData(mfccCoefficients);
                return true;
            }

            @Override
            public void processingFinished() {
                LOGGER.info("Audio processing finished.");
            }
        });
    }

    private void processMFCCData(float[] mfccCoefficients) {
        LOGGER.info("MFCC Data: " + Arrays.toString(mfccCoefficients));
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File file = new File(Environment.getExternalStorageDirectory(), "MFCC_" + timestamp + ".csv");
        try (FileWriter writer = new FileWriter(file, true)) {
            for (int i = 0; i < mfccCoefficients.length; i++) {
                writer.append(String.valueOf(mfccCoefficients[i]));
                if (i < mfccCoefficients.length - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n");
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());

        }
    }

    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            if (recordingThread != null && dispatcher != null) {
                dispatcher.stop();
                recordingThread.interrupt();
                try {
                    recordingThread.join();  // Ensure the thread has finished processing.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.warning("Thread interrupted during join");
                }
                recordingThread = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecord != null) {
            if (isRecording) {
                audioRecord.stop();
                isRecording = false;
            }
            audioRecord.release();
        }
        if (dispatcher != null) {
            dispatcher.stop();
        }
        if (recordingThread != null) {
            recordingThread.interrupt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Snackbar.make(binding.getRoot(), "Permission Denied", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}