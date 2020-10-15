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
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView openConvertToPdfActivity;
    Button givePermBtn;
    ImageView openMergePdfActivity;

    private static final int PERMISSION_RC = 1;

    private View rootView, openImagesLayout, mergePdfActivityLayout;

    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    View permissionDeniedInclude;

    void init() {
        rootView = findViewById(R.id.rootMainActLayout);
        openImagesLayout = findViewById(R.id.openImagesLayout);

        openConvertToPdfActivity = findViewById(R.id.openConvertToPdfActivity);

        mergePdfActivityLayout = findViewById(R.id.mergePdfActivityLayout);

        openMergePdfActivity = findViewById(R.id.openMergePdfActivityIV);

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


            permissionDeniedInclude.setVisibility(View.GONE);

            Utils.zoom_in(openImagesLayout, getApplicationContext());
            Utils.zoom_in(mergePdfActivityLayout, getApplicationContext());


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

    private void animateViewHorizantally(View viewTobeAnimated, View rootView) {
        viewTobeAnimated.animate()
                .translationX((rootView.getWidth() - viewTobeAnimated.getWidth()) / 2)
                //   .translationY((root.getHeight() - animatedView.getHeight()) / 2)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(700);
    }

    private void animateViewHorizantallyToView(View viewTobeAnimated, View rootView, View viewToStopAt) {
        viewTobeAnimated.animate()
                .translationX((rootView.getWidth() - viewTobeAnimated.getWidth()) / 2 - viewToStopAt.getWidth() * 2)
                //   .translationY((root.getHeight() - animatedView.getHeight()) / 2)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(500);

    }
}