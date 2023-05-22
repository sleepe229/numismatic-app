package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryHandler {
    public static void openGallery(AppCompatActivity activity, ActivityResultLauncher<Intent> galleryLauncher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    public static void handleGalleryResult(AppCompatActivity activity, Uri imageUri) {
        if (imageUri != null) {
            // Выполнение необходимых действий с URI изображения из галереи
            Toast.makeText(activity, "Выбрано изображение из галереи: " + imageUri.toString(), Toast.LENGTH_SHORT).show();
        } else {
            // Обработка ошибки
            handleError(activity, "Не удалось выбрать изображение из галереи");
        }
    }

    private static void handleError(AppCompatActivity activity, String errorMessage) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
