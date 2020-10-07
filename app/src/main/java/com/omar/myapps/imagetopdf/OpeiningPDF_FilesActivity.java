package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;

import com.omar.myapps.imagetopdf.Adapters.OpendPDF_FilesRAdapter;
import com.omar.myapps.imagetopdf.Model.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpeiningPDF_FilesActivity extends AppCompatActivity {

    String rootString = Environment.getRootDirectory().getPath();
    Uri rootUri = Uri.parse(rootString);
    List<Uri> PDF_list = new ArrayList<>();

    List<MyFile> mFileList;

    private static final int PICK_PDF_FILE_RC = 1;


    RecyclerView mergeRecycleView;
    OpendPDF_FilesRAdapter opendPDF_filesRAdapter;


    private void initRecyclerView() {
        mFileList = new ArrayList<>();
        mergeRecycleView = findViewById(R.id.openedPDF_File_RecycleView);
        opendPDF_filesRAdapter = new OpendPDF_FilesRAdapter(OpeiningPDF_FilesActivity.this, mFileList);
        mergeRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mergeRecycleView.setAdapter(opendPDF_filesRAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opeining_p_d_f__files);

        initRecyclerView();

        Search_Dir(Environment.getExternalStorageDirectory());

    }

    public void Search_Dir(File dir) {
        String pdfPattern = ".pdf";

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern)) {
                        //here you have that file.
                        Uri mPdfUri = Uri.fromFile(FileList[i]);
                        PDF_list.add(mPdfUri);
                        mFileList.add(new MyFile(mPdfUri.getLastPathSegment(), mPdfUri));
                    }
                }
            }
            opendPDF_filesRAdapter.notifyDataSetChanged();
        }
    }
}