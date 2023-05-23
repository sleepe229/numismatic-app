package com.example.myapplication;

import org.tensorflow.lite.support.label.Category;

public interface CustomRecognitionListener {
    void onResult(Category category);
}
