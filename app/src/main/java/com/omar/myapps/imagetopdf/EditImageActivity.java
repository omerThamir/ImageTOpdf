package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class EditImageActivity extends AppCompatActivity {

    private Button doneEditBTN, cancelEditButton;
    private ImageView editImageView;

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

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("ImageUri");
        editImageView.setImageURI(uri);


        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditImageActivity.this, MainActivity.class));
                finish();
            }
        });

        cancelEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditImageActivity.this, MainActivity.class));
                finish();
            }
        });

    }
}