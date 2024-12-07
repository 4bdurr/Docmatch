package com.example.dogmatch2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dogmatch2.ml.KtmKtp;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class patient extends AppCompatActivity {

    TextView result;
    ImageView imageView;
    Button picture, predictButton;
    Bitmap capturedImage;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button2);
        predictButton = findViewById(R.id.buttonUpdate);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (capturedImage != null) {
                    Bitmap scaledImage = Bitmap.createScaledBitmap(capturedImage, imageSize, imageSize, false);
                    classifyImage(scaledImage);
                } else {
                    result.setText("Gambar belum diambil");
                }
            }
        });
    }

    public void classifyImage(Bitmap image) {
        try {
            KtmKtp model = KtmKtp.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            KtmKtp.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            String[] classes;
            if (confidences.length == 2) {
                classes = new String[]{"Berhasil mengunggah foto KTP", "Berhasil mengunggah foto KTM"};
            } else if (confidences.length == 3) {
                classes = new String[]{"Berhasil mengunggah foto KTP", "Berhasil mengunggah foto KTM", "Bukan Foto KTP/KTM"};
            } else {
                result.setText("Unknown classification result");
                return;
            }

            int maxPos = 0;
            for (int i = 1; i < confidences.length; i++) {
                if (confidences[i] > confidences[maxPos]) {
                    maxPos = i;
                }
            }

            result.setText(classes[maxPos]);

            if (classes[maxPos].equals("Berhasil mengunggah foto KTP") || classes[maxPos].equals("Berhasil mengunggah foto KTM")) {
                Intent intent = new Intent(this, SuksesActivity.class);
                startActivity(intent);
            }

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            capturedImage = image;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
