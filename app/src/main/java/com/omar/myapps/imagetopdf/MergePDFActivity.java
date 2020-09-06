package com.omar.myapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import com.omar.myapps.imagetopdf.Adapters.RecyclerAdapterFile;
import com.omar.myapps.imagetopdf.Model.MyFile;

import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MergePDFActivity extends AppCompatActivity {

    String rootString = Environment.getRootDirectory().getPath();
    Uri rootUri = Uri.parse(rootString);
    List<Uri> PDF_list = new ArrayList<>();

    List<MyFile> mFileList;

    private Button mergeBtN;
    private static final int PICK_PDF_FILE_RC = 1;
    Button openPDF_FilesBtn;

    RecyclerView mergeRecycleView;
    RecyclerAdapterFile recyclerAdapterFile;


    private void initRecyclerView() {
        mFileList = new ArrayList<>();
        mergeRecycleView = findViewById(R.id.openedPDF_File_RecycleView);
        recyclerAdapterFile = new RecyclerAdapterFile(MergePDFActivity.this, mFileList, false);
        mergeRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mergeRecycleView.setAdapter(recyclerAdapterFile);
    }

    private void openPDF_Files() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri);
        startActivityForResult(Intent.createChooser(intent, "Select PDF files"), PICK_PDF_FILE_RC);
    }

   private void init() {
        openPDF_FilesBtn = findViewById(R.id.openPDF_FilesBtn);
        PDF_list = new ArrayList<>();
        initRecyclerView();
        mergeBtN = findViewById(R.id.mergePDFBtn);
        createSavingFolder();

    }

    String currentPdfFile;
    java.io.File savingfolder;

    private void createSavingFolder() {
        savingfolder = new java.io.File(Environment.getExternalStorageDirectory() +
                java.io.File.separator + "My Pdf" + java.io.File.separator + "merged PDF");

        if (!savingfolder.exists()) {
            savingfolder.mkdirs();
        }
    }

    private java.io.File createPdfFile() {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pdfFileName = "PDF_" + timeStamp + "_";

        java.io.File pdfFile = null;

        try {
            pdfFile = java.io.File.createTempFile(
                    pdfFileName,  /* prefix */
                    ".pdf",         /* suffix */
                    savingfolder      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPdfFile = pdfFile.getAbsolutePath();
        return pdfFile;
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

        mergeBtN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MergePDFActivity.this, "mergePdf here", Toast.LENGTH_SHORT).show();

                Toast.makeText(MergePDFActivity.this, PDF_list.get(0).getPath(), Toast.LENGTH_SHORT).show();
                Toast.makeText(MergePDFActivity.this, PDF_list.get(1).getPath(), Toast.LENGTH_SHORT).show();


                java.io.File outputFile = createPdfFile();
                try {
                    mergeTheFuckingPdf(PDF_list, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

             /*   try {

                    List<InputStream> list = new ArrayList<InputStream>();

                    for (Uri pdfUri : PDF_list) {
                        list.add(new FileInputStream(new java.io.File(pdfUri.getPath())));
                    }

                    java.io.File outputFile = createPdfFile();
                    OutputStream outputStream = new FileOutputStream(outputFile);
                    mergePdf(list, outputStream);
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                }

              */

            }


        });
    }

    private void mergeTheFuckingPdf(List<Uri> pdf_list, File outputFile) throws IOException, DocumentException {

        Toast.makeText(this, "doneeeee", Toast.LENGTH_SHORT).show();
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

                    mFileList.add(new MyFile(mPdfUri.getLastPathSegment(), mPdfUri));
                    recyclerAdapterFile.notifyDataSetChanged();
                } else {
                    if (data.getClipData() != null) { // multi selection
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri mPdfUri = item.getUri();
                            PDF_list.add(mPdfUri);
                            mFileList.add(new MyFile(mPdfUri.getLastPathSegment(), mPdfUri));

                        }
                        recyclerAdapterFile.notifyDataSetChanged();
                    }
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}