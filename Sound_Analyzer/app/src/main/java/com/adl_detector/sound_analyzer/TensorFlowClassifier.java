package com.adl_detector.sound_analyzer;
import android.content.Context;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TensorFlowClassifier {
    private Interpreter tflite;

    public TensorFlowClassifier(Context context) {
        try {
            tflite = new Interpreter(loadModelFile(context, "sound_classifier.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        File file = new File(context.getFilesDir(), modelName);
        FileInputStream inputStream = new FileInputStream(file);
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = 0;
        long declaredLength = file.length();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[] classify(float[] input) {
        float[][] output = new float[1][1]; // Adjust output size depending on your model's output
        tflite.run(input, output);
        return output[0];
    }
}

