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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.omar.myapps.imagetopdf.Adapters.SavedPDF_FilesRAdapter;
import com.omar.myapps.imagetopdf.Model.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavingFolderActivity extends AppCompatActivity {
    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";

    private List<MyFile> fileList = new ArrayList<>();
    RecyclerView savingFileRecycleView;
    SavedPDF_FilesRAdapter recyclerAdapterFile;

    TextView savedOrMergedFileTV;

    private void initRecyclerView() {
        savingFileRecycleView = findViewById(R.id.savingFileRecycleView);

        recyclerAdapterFile = new SavedPDF_FilesRAdapter(SavingFolderActivity.this, fileList);
        savingFileRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        savingFileRecycleView.setAdapter(recyclerAdapterFile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_folder);

        initRecyclerView();
        savedOrMergedFileTV = findViewById(R.id.savedOrMergedFileTV);

        String FolderToOpen = getIntent().getStringExtra("FolderName");
        showPDF_FilesOnRecyclerView(FolderToOpen);

        savedOrMergedFileTV.setText(FolderToOpen + " files");

        savingFileRecycleView.scrollToPosition(fileList.size() - 1);

        findViewById(R.id.returnToProcACTVBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void showPDF_FilesOnRecyclerView(String FolderName) {
        String fileUri = Environment.getExternalStorageDirectory() +
                File.separator + "My Pdf" + File.separator + FolderName + File.separator;

        File root = new File(fileUri);
        ListDir(root);
        if (fileList.size() == 0) {
            Toast.makeText(this, "there is no saved file yet ", Toast.LENGTH_SHORT).show();
        }
    }

    void ListDir(java.io.File DirectoryFile) {
        File[] files = DirectoryFile.listFiles();
        fileList.clear();
        for (File mFile : files) {
            if (!mFile.isDirectory()) {
                fileList.add(new MyFile(mFile.getName(), mFile.getPath()));
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}