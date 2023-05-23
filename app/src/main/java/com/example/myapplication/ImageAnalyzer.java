package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private Context context;
    private CustomRecognitionListener listener;
    private Interpreter interpreter;
    private TensorBuffer outputBuffer;

    public ImageAnalyzer(Context context) {
        this.context = context;
        this.listener = new MyRecognitionListener();
        try {
            Interpreter.Options options = new Interpreter.Options();
            this.interpreter = new Interpreter(loadModelFile(context), options);

            // Определение размера выходного буфера на основе модели
            int[] outputShape = interpreter.getOutputTensor(0).shape();
            int outputSize = outputShape[1]; // Предполагается форма [batchSize, outputSize]
            outputBuffer = TensorBuffer.createFixedSize(outputShape, interpreter.getOutputTensor(0).dataType());
        } catch (IOException e) {
            Log.e("ImageAnalyzer", "Ошибка: " + e.getMessage());
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void analyzeImage(Uri imageUri) {
        // Получение Bitmap из URI изображения
        Bitmap imageBitmap = getBitmapFromUri(imageUri);

        if (imageBitmap != null) {
            analyzeBitmap(imageBitmap);
        } else {
            Log.e("ImageAnalyzer", "Не удалось получить Bitmap из URI изображения");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void analyzeBitmap(Bitmap imageBitmap) {
        // Преобразование Bitmap в TensorImage
        TensorImage tensorImage = TensorImage.fromBitmap(imageBitmap);

        // Создание буфера для выходных данных модели
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(interpreter.getOutputTensor(0).shape(), interpreter.getOutputTensor(0).dataType());
        interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer());

        // Обработка выходных данных модели
        List<Category> sortedOutputs = new ArrayList<>();
        float[] probabilities = outputBuffer.getFloatArray();
        for (int i = 0; i < probabilities.length; i++) {
            Category category = new Category(String.valueOf(i), probabilities[i]);
            sortedOutputs.add(category);
        }

        // Сортировка по вероятности и передача результата в listener
        sortedOutputs.sort((item1, item2) -> Float.compare(item1.getScore(), item2.getScore()));
        listener.onResult(sortedOutputs.get(sortedOutputs.size() - 1));
    }

    private Bitmap getBitmapFromUri(Uri imageUri) {
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            return BitmapFactory.decodeStream(imageStream);
        } catch (IOException e) {
            Log.e("ImageAnalyzer", "Ошибка при получении Bitmap из URI: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        //useless
    }
}
