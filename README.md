# FlowerDetection
🤖 AI Object Detection Android App
Custom Image Detection using Teachable Machine & TensorFlow Lite

An AI-powered Android application that performs real-time object detection using a custom-trained image classification model. The model is trained using Google’s Teachable Machine and deployed on-device using TensorFlow Lite.

This project demonstrates practical implementation of Machine Learning model deployment in mobile applications, focusing on real-time inference and offline performance.

## Key Highlights

  Real-time object detection using device camera

 Custom-trained ML model integration (.tflite)

 On-device inference (No internet required)

 Android 7.0+ compatibility

 Fully customizable UI via XML layouts

 Easy model replacement through assets folder

## Tech Stack

Android Studio

Java

XML (UI Design)

TensorFlow Lite

Teachable Machine

Camera API / CameraX

## Architecture Overview
app/<br>
 ├── assets/<br>
 │    ├── model.tflite<br>
 │    └── labels.txt<br>
 ├── java/<br>
 │    └── ObjectDetectionActivity.java<br>
 ├── res/layout/<br>
 │    └── activity_main.xml<br>


The application loads the TensorFlow Lite model from the assets directory and performs real-time image classification on camera frames.
## How It Works

Launch the app and grant camera permission

Camera captures real-time frames

TensorFlow Lite model performs on-device inference

Detected objects are displayed with labels and confidence scores

Models can be swapped easily for different detection tasks
## Quick Start / Setup

Clone the repository:

git clone https://github.com/<your-username>/FlowerDetection.git


Open the project in Android Studio

Place your custom model.tflite and labels.txt in the assets/ folder

Sync Gradle to install dependencies

Build and run the app on an emulator or physical device

## Customizing the Detection System

Train a new model using Teachable Machine

Export the model as .tflite

Replace model.tflite and labels.txt in the assets/ folder

Rebuild and run the app
## Future Improvements

Multi-class object detection with higher accuracy

Export detection results as CSV or JSON

Support for multiple camera streams (front & back)

Dark mode and theme customization

Integration with cloud for model updates
