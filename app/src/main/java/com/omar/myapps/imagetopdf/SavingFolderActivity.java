package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.omar.myapps.imagetopdf.Adapters.RecyclerAdapterFile;
import com.omar.myapps.imagetopdf.Model.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavingFolderActivity extends AppCompatActivity {

    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";

    private List<MyFile> fileList = new ArrayList<>();
    RecyclerView savingFileRecycleView;
    RecyclerAdapterFile recyclerAdapterFile;

    private void initRecyclerView() {
        savingFileRecycleView = findViewById(R.id.savingFileRecycleView);

        recyclerAdapterFile = new RecyclerAdapterFile(SavingFolderActivity.this, fileList);
        savingFileRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        savingFileRecycleView.setAdapter(recyclerAdapterFile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_folder);

        initRecyclerView();

        String fileUri = Environment.getExternalStorageDirectory() +
                File.separator + "My Pdf" + File.separator;

        File root = new File(fileUri);
        ListDir(root);
    }

    void ListDir(java.io.File f) {
        java.io.File[] files = f.listFiles();
        fileList.clear();
        for (File file : files) {
            fileList.add(new MyFile(file.getName(), file.getPath()));
        }
        recyclerAdapterFile.notifyDataSetChanged();
    }

    public void openPdfFile(String fullPath) {

        File pdffile = new File(fullPath);

        Uri fileUri = FileProvider.getUriForFile(this,
                AUTHORITY, pdffile);

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(fileUri, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
            Toast.makeText(this, "please install any PDF reader", Toast.LENGTH_SHORT).show();
        }

    }

}