package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button CreateOnePageBtn, CreatemultiPagesBtn;

    void init() {

        CreateOnePageBtn = findViewById(R.id.CreateOnePageBtn);
        CreatemultiPagesBtn = findViewById(R.id.CreatemultiPagesBtn);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        CreateOnePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProcessingActivity.class));
                finish();
            }
        });

        CreatemultiPagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProcessingActivity.class));
                finish();
            }
        });
    }
}