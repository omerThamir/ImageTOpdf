package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button CreateOnePageBtn, CreateTowImgePageBtn, CreatemultiPagesBtn;

    void init() {

        CreateOnePageBtn = findViewById(R.id.CreateOnePageBtn);
        CreatemultiPagesBtn = findViewById(R.id.CreatemultiPagesBtn);
        CreateTowImgePageBtn = findViewById(R.id.Create2imagePerPageBtn);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        CreateOnePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CreateTowImgePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
                intent.putExtra("image_per_page", 2);
                startActivity(intent);
                finish();
            }
        });

        CreatemultiPagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
                intent.putExtra("image_per_page", 4);
                startActivity(intent);
                finish();
            }
        });
    }
}