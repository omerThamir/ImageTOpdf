package com.omar.myapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import com.omar.myapps.imagetopdf.Adapters.OpendPDF_FilesRAdapter;
import com.omar.myapps.imagetopdf.Adapters.SelectedPDF_toMergeRAdapter;
import com.omar.myapps.imagetopdf.Model.MyFile;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.omar.myapps.imagetopdf.Model.MySelectedFiles;


public class MergePDFActivity extends AppCompatActivity {

    public List<Uri> PDF_UriList = new ArrayList<>();

    List<MyFile> mFileList;

    public List<MySelectedFiles> mSelectedFileList;

    RecyclerView mergeRecycleView, selectedPDFtoMergeRecyclerView;
    OpendPDF_FilesRAdapter opendPDF_filesRAdapter;

    View openingPDF_FilesLayout;

    ItemTouchHelper.Callback ithCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Collections.swap(PDF_UriList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

            // get the viewHolder's and target's positions in your adapter data, swap them
            Collections.swap(mSelectedFileList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            // and notify the adapter that its data set has changed
            selectedPDF_toMergeRAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };


    private void initRecyclerView() {
        mergeRecycleView = findViewById(R.id.openedPDF_File_RecycleView);
        mergeRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    private void reInitRecyclerView() {
        mFileList = new ArrayList<>();
        opendPDF_filesRAdapter = new OpendPDF_FilesRAdapter(MergePDFActivity.this, mFileList);
        mergeRecycleView.setAdapter(opendPDF_filesRAdapter);
    }

    SelectedPDF_toMergeRAdapter selectedPDF_toMergeRAdapter;

    private void initSelectedPdfRecyclerView() {
        mSelectedFileList = new ArrayList<>();
        selectedPDFtoMergeRecyclerView = findViewById(R.id.selectedPDFtoMergeRecyclerView);
        selectedPDF_toMergeRAdapter = new SelectedPDF_toMergeRAdapter(MergePDFActivity.this, mSelectedFileList);
        selectedPDFtoMergeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        selectedPDFtoMergeRecyclerView.setAdapter(selectedPDF_toMergeRAdapter);
    }


    ImageView openPDF_FilesBtn, mergeBtN, newProjectBtn, ShowMergePDFBtn;

    View openPdfFileLayout, mergePdfFileLayout, newProjectMergeLayout, savedPdfMergeLayout;

    private ConstraintLayout selectedPdfsMergeLayout;

    private void init() {
        openPDF_FilesBtn = findViewById(R.id.openPDF_FilesBtn);
        ShowMergePDFBtn = findViewById(R.id.showMergePDFBtn);
        newProjectBtn = findViewById(R.id.newProjectBtn);
        selectedPdfsMergeLayout = findViewById(R.id.selectedPdfsMergeLayout);

        openPdfFileLayout = findViewById(R.id.openPdfFileLayout);
        mergePdfFileLayout = findViewById(R.id.mergePdfFileLayout);
        newProjectMergeLayout = findViewById(R.id.newProjectMergeLayout);
        savedPdfMergeLayout = findViewById(R.id.savedPdfMergeLayout);

        PDF_UriList = new ArrayList<>();

        initRecyclerView();
        reInitRecyclerView();

        initSelectedPdfRecyclerView();

        mergeBtN = findViewById(R.id.mergePDFBtn);
        createSavingFolder();
        openingPDF_FilesLayout = findViewById(R.id.opening_pdf_files_layout);
    }

    String currentPdfFile;
    java.io.File savingfolder;

    private void createSavingFolder() {
        savingfolder = new java.io.File(Environment.getExternalStorageDirectory() +
                java.io.File.separator + "My Pdf" + java.io.File.separator + "Merged");

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

    private ProgressDialog progDailog;

    private void initProgDailog() {
        progDailog = new ProgressDialog(MergePDFActivity.this, R.style.AppCompatAlertDialogStyle);
        progDailog.setMessage("Processing...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();
    }

    class MyAsyncTask extends AsyncTask<Object, String, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            preparePDFsAndMergeThem();
            return objects;
        }

        @Override
        protected void onPostExecute(Object o) {
            progDailog.dismiss();
            showMegedFiles();
            Toast.makeText(MergePDFActivity.this, "done", Toast.LENGTH_SHORT).show();
            Utils.pdfMergingIsDone = true;
        }
    }

    class AsyncTaskForOpenPDS extends AsyncTask<Object, String, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            Search_Dir(Environment.getExternalStorageDirectory());
            return objects;
        }

        @Override
        protected void onPostExecute(Object o) {
            progDailog.dismiss();
            opendPDF_filesRAdapter.notifyDataSetChanged();
        }
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
                        //   PDF_list.add(mPdfUri);
                        mFileList.add(new MyFile(mPdfUri.getLastPathSegment(), mPdfUri));
                    }
                }
            }
            //   opendPDF_filesRAdapter.notifyDataSetChanged(); this was here before using async task
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_p_d_f);

        init();

        Utils.zoom_in(openPdfFileLayout, getApplicationContext());


        // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
        ItemTouchHelper ith = new ItemTouchHelper(ithCallback);
        ith.attachToRecyclerView(selectedPDFtoMergeRecyclerView);

        openPDF_FilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (opendPDF_filesRAdapter != null) {
                    reInitRecyclerView();
                }

                openPdfFileLayout.setVisibility(View.GONE);
                openingPDF_FilesLayout.setVisibility(View.VISIBLE);

                AsyncTaskForOpenPDS asyncTaskForOpenPDS = new AsyncTaskForOpenPDS();
                initProgDailog();
                asyncTaskForOpenPDS.execute(null, null);


            }
        });

        mergeBtN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                initProgDailog();
                myAsyncTask.execute(null, null);

            }


        });

        ShowMergePDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMegedFiles();
            }
        });


        findViewById(R.id.selectPDFBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PDF_UriList.size() <= 1) {
                    Toast.makeText(MergePDFActivity.this, "you have to select 2 files at least", Toast.LENGTH_SHORT).show();
                } else {
                    openingPDF_FilesLayout.setVisibility(View.GONE);
                    openPDF_FilesBtn.setVisibility(View.GONE);

                    mergePdfFileLayout.setVisibility(View.VISIBLE);
                    ShowMergePDFBtn.setVisibility(View.VISIBLE);
                    Utils.zoom_in(mergePdfFileLayout, getApplicationContext());

                    selectedPdfsMergeLayout.setVisibility(View.VISIBLE);
                    selectedPDF_toMergeRAdapter.notifyDataSetChanged();
                }
            }
        });

        findViewById(R.id.cancelPDFSelectionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingPDF_FilesLayout.setVisibility(View.GONE);
                openPdfFileLayout.setVisibility(View.VISIBLE);
                ShowMergePDFBtn.setVisibility(View.VISIBLE);
                PDF_UriList.clear();
            }
        });

        newProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishingProject();
                newProjectMergeLayout.setVisibility(View.GONE);
            }
        });
    }

    private void preparePDFsAndMergeThem() {
        java.io.File outputFile = createPdfFile();

        try {
            List<InputStream> list = new ArrayList<InputStream>();
            for (Uri pdfUri : PDF_UriList) {
                InputStream inputStreamOne = new FileInputStream(new File(pdfUri.getPath()));
                list.add(inputStreamOne);
            }

            OutputStream outputStream = new FileOutputStream(outputFile);
            mergePdf(list, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMegedFiles() {
        Intent intent = new Intent(MergePDFActivity.this, SavingFolderActivity.class);
        intent.putExtra("FolderName", "Merged");
        startActivity(intent);
    }


    private static void mergePdf(List<InputStream> list, OutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();

        document.setMargins(3, 3, 3, 3);

        for (InputStream inputStream : list) {
            PdfReader pdfReader = new PdfReader(inputStream);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                document.newPage();
                PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
                pdfContentByte.addTemplate(page, 0, 0);
            }
        }

        outputStream.flush();
        document.close();
        outputStream.close();
    }


    @Override
    protected void onResume() {

        super.onResume();
        if (Utils.pdfMergingIsDone

                && newProjectMergeLayout.getVisibility() != View.VISIBLE) {

            mergePdfFileLayout.setVisibility(View.GONE);

            showAndAnimateNewProjectAndShowSavedFileLayout();
        }
    }

    private void showAndAnimateNewProjectAndShowSavedFileLayout() {
        newProjectMergeLayout.setVisibility(View.VISIBLE);
        Utils.zoom_in(newProjectMergeLayout, getApplicationContext());
        savedPdfMergeLayout.setVisibility(View.VISIBLE);
        Utils.zoom_in(savedPdfMergeLayout, getApplicationContext());
    }

    void finishingProject() {
        if (mSelectedFileList != null) {
            mSelectedFileList.clear();
        }
        if (PDF_UriList != null) {
            PDF_UriList.clear();
        }

        if (mFileList != null) {
            mFileList.clear();
        }

        openPDF_FilesBtn.setVisibility(View.VISIBLE);
        mergePdfFileLayout.setVisibility(View.GONE);
        selectedPdfsMergeLayout.setVisibility(View.GONE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishingProject();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }
}