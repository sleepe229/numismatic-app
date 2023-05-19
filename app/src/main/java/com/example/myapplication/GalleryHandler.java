package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryHandler {
    public static void openGallery(AppCompatActivity activity, ActivityResultLauncher<String> galleryLauncher) {
        galleryLauncher.launch("image/*");
    }

    public static void handleGalleryResult(AppCompatActivity activity, Uri imageUri) {
        if (imageUri != null) {
            // Выполнение необходимых действий с URI изображения из галереи
            Toast.makeText(activity, "Выбрано изображение из галереи: " + imageUri.toString(), Toast.LENGTH_SHORT).show();
        } else {
            // Обработка ошибки
            Toast.makeText(activity, "Не удалось выбрать изображение из галереи", Toast.LENGTH_SHORT).show();
        }
    }
}
