package com.example.flowerdetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView resultTextView;
    private TextView confidenceTextView;
    private Button takePictureButton;

    private Bitmap currentBitmap;
    private Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.result);
        confidenceTextView = findViewById(R.id.confidence);
        takePictureButton = findViewById(R.id.button);

        // Set initial text
        resultTextView.setText("Take a picture to classify");
        confidenceTextView.setText("");

        // DEBUG: List all files in assets
        try {
            String[] files = getAssets().list("");
            Log.d("MainActivity", "Files in assets folder: " + java.util.Arrays.toString(files));
        } catch (IOException e) {
            Log.e("MainActivity", "Error listing assets", e);
        }

        // Initialize classifier
        try {
            classifier = new Classifier(this);
            Toast.makeText(this, "Model loaded successfully", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Classifier initialized: " + classifier.isLoaded());
        } catch (Exception e) {
            String errorMsg = "Error loading model: " + e.getMessage();
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            Log.e("MainActivity", errorMsg, e);

            // Show error in UI
            resultTextView.setText("Load Error");
            confidenceTextView.setText(e.getMessage());
        }

        // Set button click listener
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                currentBitmap = (Bitmap) data.getExtras().get("data");
                if (currentBitmap != null) {
                    imageView.setImageBitmap(currentBitmap);
                    classifyImage(currentBitmap);
                }
            }
        }
    }

    private void classifyImage(Bitmap image) {
        if (classifier != null && classifier.isLoaded()) {
            // Show classifying message
            resultTextView.setText("Classifying...");
            confidenceTextView.setText("");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("MainActivity", "Starting classification in background thread");
                        final List<Classification> results = classifier.classify(image);
                        Log.d("MainActivity", "Classification completed. Results: " + (results != null ? results.size() : "null"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayResults(results);
                            }
                        });
                    } catch (Exception e) {
                        Log.e("MainActivity", "Background classification error", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resultTextView.setText("Error");
                                confidenceTextView.setText("Classification failed");
                                Toast.makeText(MainActivity.this, "Classification error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Classifier not initialized", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Classifier is null or not loaded");
        }
    }

    private void displayResults(List<Classification> results) {
        if (results == null || results.isEmpty()) {
            resultTextView.setText("No results");
            confidenceTextView.setText("Try another image");
            Log.d("MainActivity", "No results to display");
            return;
        }

        // Display top result
        Classification topResult = results.get(0);
        String topLabel = topResult.getLabel();
        float topConfidence = topResult.getConfidence();

        resultTextView.setText(topLabel);

        // Display top 3 confidences
        StringBuilder confidenceText = new StringBuilder();
        for (int i = 0; i < Math.min(results.size(), 3); i++) {
            Classification result = results.get(i);
            confidenceText.append(result.getLabel())
                    .append(": ")
                    .append(String.format(Locale.getDefault(), "%.1f", result.getConfidence() * 100))
                    .append("%\n");
        }
        confidenceTextView.setText(confidenceText.toString());

        // Show success message
        Toast.makeText(this, "Classification complete!", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "Displayed results: " + topLabel + " (" + (topConfidence * 100) + "%)");
    }
}