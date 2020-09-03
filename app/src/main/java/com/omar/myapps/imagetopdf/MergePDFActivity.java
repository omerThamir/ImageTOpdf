package com.omar.myapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MergePDFActivity extends AppCompatActivity {


    private static final int PICK_PDF_FILE_RC = 1;
    Button openPDF_FilesBtn;

    String rootString = Environment.getRootDirectory().getPath();
    Uri rootUri = Uri.parse(rootString);
    List<Uri> PDF_list;

    private void openPDF_Files() {

        // intent.setAction(Intent.ACTION_GET_CONTENT);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri);
        startActivityForResult(Intent.createChooser(intent, "Select PDF files"), PICK_PDF_FILE_RC);
    }

    void init() {
        openPDF_FilesBtn = findViewById(R.id.openPDF_FilesBtn);
        PDF_list = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_p_d_f);

        init();

        openPDF_FilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPDF_Files();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            // When pdf is picked
            if (requestCode == PICK_PDF_FILE_RC && resultCode == RESULT_OK) {
                // Get the Image from data one image

                if (data.getData() != null) {

                    Uri mPdfUri = data.getData();

                    PDF_list.add(mPdfUri);
                    Toast.makeText(this, "" + PDF_list.size(), Toast.LENGTH_SHORT).show();

                } else {
                    if (data.getClipData() != null) { // multi selection
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri mPdfUri = item.getUri();
                            PDF_list.add(mPdfUri);
                        }
                        Toast.makeText(this, "" + PDF_list.size(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}