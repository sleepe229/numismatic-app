package com.example.myapplication;

import org.tensorflow.lite.support.label.Category;

public class MyRecognitionListener implements CustomRecognitionListener {
    @Override
    public void onResult(Category category) {
        //реализация обработки результата распознавания
    }
}