package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryHandler {
    public static void openGallery(AppCompatActivity activity, ActivityResultLauncher<Intent> galleryLauncher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void handleGalleryResult(AppCompatActivity activity, ActivityResult result, ImageAnalyzer imageAnalyzer) {
        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
            Uri imageUri = result.getData().getData();
            if (imageUri != null) {
                Toast.makeText(activity, "Выбрано изображение из галереи: " + imageUri.toString(), Toast.LENGTH_SHORT).show();
                imageAnalyzer.analyzeImage(imageUri);
            } else {
                handleError(activity, "Не удалось выбрать изображение из галереи");
            }
        } else {
            handleError(activity, "Отменено выбор из галереи");
        }
    }

    private static void handleError(AppCompatActivity activity, String errorMessage) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
