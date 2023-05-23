package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CameraHandler {
    private static final int REQUEST_CAMERA = 1;

    public static void openCamera(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            launcher.launch(takePictureIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void handleCameraResult(AppCompatActivity activity, ActivityResult result, ImageAnalyzer imageAnalyzer) {
        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.hasExtra(MediaStore.EXTRA_OUTPUT)) {
                Uri imageUri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                imageAnalyzer.analyzeImage(imageUri);
            } else if (data != null && data.hasExtra("data")) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                // Выполнение необходимых действий с битмапом изображения
                imageAnalyzer.analyzeBitmap(imageBitmap);
            }
        } else if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED) {
            Toast.makeText(activity, "Съемка отменена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Не удалось получить изображение с камеры", Toast.LENGTH_SHORT).show();
        }
    }
}
