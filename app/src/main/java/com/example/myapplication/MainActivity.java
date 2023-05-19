package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<String> galleryLauncher; // Добавлено поле galleryLauncher

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.camera_button);
        galleryButton = findViewById(R.id.gallery_button);

        cameraButton.setOnClickListener(v -> openCamera());
        galleryButton.setOnClickListener(v -> openGallery());

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> handleCameraResult(result));
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> handleGalleryResult(result));
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

    private void handleGalleryResult(Uri result) {
        GalleryHandler.handleGalleryResult(this, result);
    }
}
