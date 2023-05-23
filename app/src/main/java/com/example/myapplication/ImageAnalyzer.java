package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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

public class ImageAnalyzer {
    private Context context;
    private CustomRecognitionListener listener;
    private Interpreter interpreter;
    private TensorBuffer outputBuffer;

    public ImageAnalyzer(Context context) {
        this.context = context;
        this.listener = new MyRecognitionListener();
        try {
            this.interpreter = new Interpreter(loadModelFile(context));

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
        Bitmap imageBitmap = getBitmapFromUri(imageUri);

        if (imageBitmap != null) {
            analyzeBitmap(imageBitmap);
        } else {
            Log.e("ImageAnalyzer", "Не удалось получить Bitmap из URI изображения");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void analyzeBitmap(Bitmap imageBitmap) {
        TensorImage tensorImage = TensorImage.fromBitmap(imageBitmap);
        tensorImage = preprocessImage(tensorImage);

        interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer());

        List<Category> sortedOutputs = new ArrayList<>();
        float[] outputData = outputBuffer.getFloatArray();
        for (int i = 0; i < outputData.length; i++) {
            Category category = new Category(String.valueOf(i), outputData[i]);
            sortedOutputs.add(category);
        }

        sortedOutputs.sort((item1, item2) -> Float.compare(item1.getScore(), item2.getScore()));
        Category resultCategory = sortedOutputs.get(sortedOutputs.size() - 1);
        Log.d("ImageAnalyzer", "Recognition result: " + resultCategory.getLabel() + ", Score: " + resultCategory.getScore());
        listener.onResult(resultCategory);
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

    private TensorImage preprocessImage(TensorImage image) {
        int targetWidth = 300;
        int targetHeight = 300;
        Bitmap.Config targetConfig = Bitmap.Config.ARGB_8888;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(image.getBitmap(), targetWidth, targetHeight, true);

        if (resizedBitmap.getConfig() != targetConfig) {
            Bitmap argbBitmap = resizedBitmap.copy(targetConfig, true);
            return TensorImage.fromBitmap(argbBitmap);
        } else {
            return TensorImage.fromBitmap(resizedBitmap);
        }
    }

}

