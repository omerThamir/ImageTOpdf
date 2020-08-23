package com.omar.myapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_GALLERY = 1;
    private static final int TAKE_PHOTO_RC = 2;


    private RecyclerView recyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private List<MyImage> mImageUris;

    private List<Bitmap> bitmapList;

    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        mImageUris = new ArrayList<>();
        myRecyclerAdapter = new MyRecyclerAdapter(MainActivity.this, mImageUris);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(myRecyclerAdapter);
    }

    ImageView imageView;


    Uri imageUri;
    Bitmap bitmap;


    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 3;

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        startActivityForResult(intent, PICK_PDF_FILE);
    }


    private void requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, 100);
        }
    }

    private ProgressDialog progDailog;

    String currentPhotoPath;

    public void displayImageToEdit(int imageIndex) {
        imageView.setImageBitmap(bitmapList.get(imageIndex));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        imageView = findViewById(R.id.imageView);

        requestRequiredPermissions();
        progDailog = new ProgressDialog(MainActivity.this);

        findViewById(R.id.openImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });


        findViewById(R.id.convertToPdfBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();

                progDailog.setMessage("Processing...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();
                myAsyncTask.execute(null, null, null, null);
            }
        });
    }

    Uri savedPdfFileUri;
    String currentPdfFile;

    private File createPdfFile() {
        File savingfolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "My Pdf");

        if (!savingfolder.exists()) {
            savingfolder.mkdirs();
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pdfFileName = "PDF_" + timeStamp + "_";

        File pdfFile = null;

        try {
            pdfFile = File.createTempFile(
                    pdfFileName,  /* prefix */
                    ".pdf",         /* suffix */
                    savingfolder      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPdfFile = pdfFile.getAbsolutePath();
        savedPdfFileUri = Uri.fromFile(pdfFile);
        return pdfFile;
    }

    private void convertImageToPdf() {

        PdfDocument pdfDocument = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pdfDocument = new PdfDocument();

            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();

            PdfDocument.Page page = pdfDocument.startPage(myPageInfo);

            page.getCanvas().drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            File myPDFFile = createPdfFile();

            try {

                pdfDocument.writeTo(new FileOutputStream(myPDFFile));

            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder imagePicker = new AlertDialog.Builder(this);
        imagePicker.setTitle("Pick Image");

        String[] imagePickerItems = {"from gallery", "from camera"};

        imagePicker.setItems(imagePickerItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        PickImageFromGallary();
                        return;

                    case 1:
                        takePictureFromCamera();
                        return;
                }
            }
        });
        imagePicker.show();
    }

    public void PickImageFromGallary() {
    /*    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, RC_GALLERY);
     */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_GALLERY);
    }

    private void takePictureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "an error has happened", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        AUTHORITY, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, TAKE_PHOTO_RC);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == RC_GALLERY && resultCode == RESULT_OK) {
                // Get the Image from data one image

                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    bitmapList = new ArrayList<>();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmapList.add(ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), mImageUri)));
                    } else {
                        bitmapList.add(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri));
                    }

                    mImageUris.add(new MyImage(mImageUri));
                    myRecyclerAdapter.notifyDataSetChanged();
                } else {
                    if (data.getClipData() != null) { // multi selection
                        ClipData mClipData = data.getClipData();

                        bitmapList = new ArrayList<>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri mImageUri = item.getUri();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                bitmapList.add(ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), mImageUri)));
                            } else {
                                bitmapList.add(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri));
                            }

                            mImageUris.add(new MyImage(mImageUri));
                        }
                        myRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }


            /**
             try {
             imageUri = data.getData();
             bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
             //   imageView.setImageBitmap(bitmap);

             imageView.setRotation(getCameraPhotoOrientation(imageUri.getPath()));
             imageView.setImageURI(imageUri);

             } catch (IOException e) {
             e.printStackTrace();
             }

             */

            else if (requestCode == 2) {

                imageUri = data.getData();
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");

                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    class MyAsyncTask extends AsyncTask<Object, String, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            convertImageToPdf();
            return params;
        }

        @Override
        protected void onPostExecute(Object result) {
            progDailog.dismiss();
            Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
        }
    }

    public static int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 90;
                    break;
                default:
                    rotate = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

}