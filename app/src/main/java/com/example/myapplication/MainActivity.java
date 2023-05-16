package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private Button cameraButton;
    private Button galleryButton;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.camera_button);
        galleryButton = findViewById(R.id.gallery_button);

        // Инициализация запускателей результатов активности
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                handleCameraResult(result);
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                handleGalleryResult(result.getData());
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Запрос разрешения на использование камеры, если оно не предоставлено
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            // Запуск интента камеры
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    private void handleCameraResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
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
        } else if (result.getResultCode() == RESULT_CANCELED) {
            // Обработка отмены
            Toast.makeText(this, "Съемка отменена", Toast.LENGTH_SHORT).show();
        } else {
            // Обработка ошибки
            Toast.makeText(this, "Не удалось получить изображение с камеры", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleGalleryResult(Intent data) {
        if (data != null && data.getData() != null) {
            // Обработка изображения из галереи
            Uri imageUri = data.getData();
            // Выполнение необходимых действий с URI изображения
        } else {
            // Обработка ошибки
            Toast.makeText(this, "Не удалось выбрать изображение из галереи", Toast.LENGTH_SHORT).show();
        }
    }
}