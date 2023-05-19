package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CameraHandler {
    private static final int REQUEST_CAMERA = 1;

    private static ActivityResultLauncher<Intent> cameraLauncher;

    public static void openCamera(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Запросить разрешение на использование камеры, если оно не предоставлено
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            // Запуск интента камеры
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> handleCameraResult(activity, result));
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private static void handleCameraResult(AppCompatActivity activity, ActivityResult result) {
        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.hasExtra(MediaStore.EXTRA_OUTPUT)) {
                // Обработка изображения с камеры
                Uri imageUri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                // Выполнение необходимых действий с URI изображения
            } else if (data != null && data.hasExtra("data")) {
                // Обработка миниатюры изображения
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                // Выполнение необходимых действий с битмапом изображения
            }
        } else if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED) {
            // Обработка отмены
            Toast.makeText(activity, "Съемка отменена", Toast.LENGTH_SHORT).show();
        } else {
            // Обработка ошибки
            Toast.makeText(activity, "Не удалось получить изображение с камеры", Toast.LENGTH_SHORT).show();
        }
    }
}
