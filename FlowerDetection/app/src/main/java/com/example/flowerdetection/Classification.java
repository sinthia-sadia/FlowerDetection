package com.example.flowerdetection;

public class Classification {
    private final String label;
    private final float confidence;

    public Classification(String label, float confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    public String getLabel() {
        return label;
    }

    public float getConfidence() {
        return confidence;
    }
}