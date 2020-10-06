package com.omar.myapps.imagetopdf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button openConvertToPdfActivity, openMergePdfActivity, givePermBtn;
    private static final int PERMISSION_RC = 1;

    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    View permissionDeniedInclude;

    void init() {
        openConvertToPdfActivity = findViewById(R.id.openConvertToPdfActivity);
        openMergePdfActivity = findViewById(R.id.openMergePdfActivity);
        permissionDeniedInclude = findViewById(R.id.permissionDeniedInclude);
        givePermBtn = findViewById(R.id.givePermBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (!hasPermissions(this, PERMISSIONS)) {

            openConvertToPdfActivity.setVisibility(View.GONE);
            permissionDeniedInclude.setVisibility(View.VISIBLE);
        } else {

            openConvertToPdfActivity.setVisibility(View.VISIBLE);
            permissionDeniedInclude.setVisibility(View.GONE);

            Utils.zoom_in(openConvertToPdfActivity,getApplicationContext());
        }


        openConvertToPdfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        openMergePdfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, MergePDFActivity.class);
                startActivity(intent);
                finish();
            }
        });


        givePermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RC);
            }
        });


        findViewById(R.id.exitAppBtn).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_RC:

                if (hasPermissions(getApplicationContext(), permissions)) {
                    openConvertToPdfActivity.setVisibility(View.VISIBLE);
                    permissionDeniedInclude.setVisibility(View.GONE);
                }
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}