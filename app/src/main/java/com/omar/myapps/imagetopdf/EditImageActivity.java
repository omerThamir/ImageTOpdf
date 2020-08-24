package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class EditImageActivity extends AppCompatActivity {

    private Button doneEditBTN, cancelEditButton;
    private ImageView editImageView;
    Uri imageUri;
    private float[] flipVertical = {1.0f, -1.0f};
    private float[] flipHorizontal = {-1.0f, 1.0f};

    private void init() {
        doneEditBTN = findViewById(R.id.EditDoneBTN);
        cancelEditButton = findViewById(R.id.EditCancelBTN);
        editImageView = findViewById(R.id.editImageView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        init();

        displaySentImage();


        doneEditBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditImageActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        cancelEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditImageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.rotateIBNplus90).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateImage(90);
            }
        });

        findViewById(R.id.rotateIBNminus90).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateImage(-90);
            }
        });

        findViewById(R.id.flipVerticalIBN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipImage(flipVertical);
            }
        });

        findViewById(R.id.flipHorizontalIBN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipImage(flipHorizontal);
            }
        });
    }


    private void flipImage(float flipType[]) {
        // the 3 lines below used to get bitmap from image view
        editImageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) editImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Matrix matrix = new Matrix();
        matrix.preScale(flipType[0], flipType[1]);

        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);

        editImageView.setImageBitmap(rotated);
    }

    private void rotateImage(float degree) {

        // the 3 lines below used to get bitmap from image view
        editImageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) editImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);

        editImageView.setImageBitmap(rotated);

    }

    private void displaySentImage() {
        Intent intent = getIntent();
        if (intent.hasExtra("ImageUri")) {
            imageUri = intent.getParcelableExtra("ImageUri");
            editImageView.setImageURI(imageUri);
        } else {
            startActivity(new Intent(EditImageActivity.this, MainActivity.class));
            this.finish();
        }
    }
}