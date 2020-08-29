package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button openConvertToPdfActivity;

    void init() {
        openConvertToPdfActivity = findViewById(R.id.openConvertToPdfActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        openConvertToPdfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}