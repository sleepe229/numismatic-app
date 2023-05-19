package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryHandler {
    private static ActivityResultLauncher<Intent> galleryLauncher;

    public static void openGallery(AppCompatActivity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                handleGalleryResult(activity, result.getData());
            }
        });
        galleryLauncher.launch(galleryIntent);
    }

    private static void handleGalleryResult(AppCompatActivity activity, Intent data) {
        if (data != null && data.getData() != null) {
            // Обработка изображения из галереи
            Uri imageUri = data.getData();
            // Выполнение необходимых действий с URI изображения
        } else {
            // Обработка ошибки
            Toast.makeText(activity, "Не удалось выбрать изображение из галереи", Toast.LENGTH_SHORT).show();
        }
    }
}
