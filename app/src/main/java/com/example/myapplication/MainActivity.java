package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button cameraButton;
    private Button galleryButton;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 2;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.camera_button);
        galleryButton = findViewById(R.id.gallery_button);

        cameraButton.setOnClickListener(v -> openCamera());
        galleryButton.setOnClickListener(v -> openGallery());

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleCameraResult);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleGalleryResult);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            CameraHandler.openCamera(this, cameraLauncher);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        } else {
            GalleryHandler.openGallery(this, galleryLauncher);
        }
    }

    private void handleCameraResult(ActivityResult result) {
        CameraHandler.handleCameraResult(this, result);
    }

    private void handleGalleryResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Uri imageUri = result.getData().getData();
            GalleryHandler.handleGalleryResult(this, imageUri);
        } else {
            Toast.makeText(this, "Не удалось выбрать изображение из галереи", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraHandler.openCamera(this, cameraLauncher);
                } else {
                    Toast.makeText(this, "Отсутствуют разрешения для использования камеры", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GalleryHandler.openGallery(this, galleryLauncher);
                } else {
                    Toast.makeText(this, "Отсутствуют разрешения для чтения изображений", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
