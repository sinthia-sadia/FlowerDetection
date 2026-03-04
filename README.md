# FlowerDetection
🤖 AI Object Detection Android App
Custom Image Detection using Teachable Machine & TensorFlow Lite

An AI-powered Android application that performs real-time object detection using a custom-trained image classification model. The model is trained using Google’s Teachable Machine and deployed on-device using TensorFlow Lite.

This project demonstrates practical implementation of Machine Learning model deployment in mobile applications, focusing on real-time inference and offline performance.

🚀 Key Highlights

📷 Real-time object detection using device camera

🧠 Custom-trained ML model integration (.tflite)

⚡ On-device inference (No internet required)

📱 Android 7.0+ compatibility

🎨 Fully customizable UI via XML layouts

🔄 Easy model replacement through assets folder

🛠 Tech Stack

Android Studio

Java

XML (UI Design)

TensorFlow Lite

Teachable Machine

Camera API / CameraX

📂 Architecture Overview
app/
 ├── assets/
 │    ├── model.tflite
 │    └── labels.txt
 ├── java/
 │    └── ObjectDetectionActivity.java
 ├── res/layout/
 │    └── activity_main.xml


The application loads the TensorFlow Lite model from the assets directory and performs real-time image classification on camera frames.

To personalize the detection system:

Train a new model using Teachable Machine

Export as .tflite

Replace model.tflite and labels.txt in assets

Rebuild and run

🧠 What I Learned

End-to-end ML workflow (Data → Training → Export → Deployment)

Integrating TensorFlow Lite into Android

Handling real-time camera frame processing

Optimizing inference for mobile performance

Structuring scalable Android projects

🎯 Purpose

This project was built for educational and portfolio purposes to explore the intersection of:

Mobile Development × Machine Learning × Real-Time AI

It reflects my growing interest in ML deployment, intelligent applications, and Android-based AI solutions.
