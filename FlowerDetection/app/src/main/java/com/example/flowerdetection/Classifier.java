package com.example.flowerdetection;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Classifier {

    private static final String TAG = "Classifier";
    private static final String MODEL_FILE = "model.tflite";
    private static final String LABEL_FILE = "labels.txt";

    private static final int INPUT_SIZE = 224;

    private org.tensorflow.lite.Interpreter tflite;
    private final List<String> labels;

    public Classifier(Context context) throws IOException {
        Log.d(TAG, "Starting model loading...");

        // Debug: List all files in assets
        AssetManager assetManager = context.getAssets();
        String[] files = assetManager.list("");
        if (files != null) {
            Log.d(TAG, "Files in assets folder: " + java.util.Arrays.toString(files));

            // Check if our files exist
            boolean modelFound = false;
            boolean labelsFound = false;
            for (String file : files) {
                if (file.equals(MODEL_FILE)) {
                    modelFound = true;
                    Log.d(TAG, "✓ Found model file: " + MODEL_FILE);
                }
                if (file.equals(LABEL_FILE)) {
                    labelsFound = true;
                    Log.d(TAG, "✓ Found labels file: " + LABEL_FILE);
                }
            }

            if (!modelFound) {
                throw new IOException("Model file not found: " + MODEL_FILE);
            }
            if (!labelsFound) {
                throw new IOException("Labels file not found: " + LABEL_FILE);
            }
        } else {
            throw new IOException("Cannot access assets folder");
        }

        // Load labels
        labels = FileUtil.loadLabels(context, LABEL_FILE);
        Log.d(TAG, "Labels loaded successfully. Count: " + labels.size());
        for (int i = 0; i < labels.size(); i++) {
            Log.d(TAG, "Label " + i + ": " + labels.get(i));
        }

        if (labels.isEmpty()) {
            throw new IOException("Labels file is empty");
        }

        // Load model
        ByteBuffer modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE);
        Log.d(TAG, "Model loaded. Size: " + modelBuffer.capacity() + " bytes");

        // Add interpreter options for better compatibility
        org.tensorflow.lite.Interpreter.Options options = new org.tensorflow.lite.Interpreter.Options();
        options.setNumThreads(4);

        tflite = new org.tensorflow.lite.Interpreter(modelBuffer, options);
        Log.d(TAG, "✓ TFLite interpreter created successfully");
    }

    public List<Classification> classify(Bitmap bitmap) {
        if (tflite == null) {
            Log.e(TAG, "Interpreter is null");
            return new ArrayList<>();
        }

        try {
            Log.d(TAG, "Starting classification...");
            Log.d(TAG, "Original bitmap size: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            // Try FLOAT32 instead of UINT8 (better compatibility)
            TensorImage inputImage = new TensorImage(org.tensorflow.lite.DataType.FLOAT32);
            inputImage.load(bitmap);

            // Preprocess - resize to model input size
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                    .build();

            TensorImage processedImage = imageProcessor.process(inputImage);
            Log.d(TAG, "Image processed. Size: " + INPUT_SIZE + "x" + INPUT_SIZE);

            // Run inference
            float[][] output = new float[1][labels.size()];
            tflite.run(processedImage.getBuffer(), output);

            // Log ALL outputs for debugging
            StringBuilder outputLog = new StringBuilder("All outputs: ");
            for (int i = 0; i < output[0].length; i++) {
                outputLog.append(String.format(Locale.getDefault(), "%.4f ", output[0][i]));
            }
            Log.d(TAG, outputLog.toString());

            List<Classification> results = getSortedResults(output[0]);
            Log.d(TAG, "Results count: " + results.size());

            return results;

        } catch (Exception e) {
            Log.e(TAG, "Classification error: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private List<Classification> getSortedResults(float[] probabilities) {
        List<Classification> results = new ArrayList<>();

        for (int i = 0; i < labels.size(); i++) {
            results.add(new Classification(labels.get(i), probabilities[i]));
        }

        // Sort by confidence (highest first)
        results.sort((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));

        // Log top result
        if (!results.isEmpty()) {
            Classification top = results.get(0);
            Log.d(TAG, "Top result: " + top.getLabel() + " (" + String.format(Locale.getDefault(), "%.1f", top.getConfidence() * 100) + "%)");
        }

        return results;
    }

    public boolean isLoaded() {
        return tflite != null;
    }
}