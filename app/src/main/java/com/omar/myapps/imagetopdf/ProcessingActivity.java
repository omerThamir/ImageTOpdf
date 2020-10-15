package com.omar.myapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.omar.myapps.imagetopdf.Adapters.OpenedImagesRAdapter;
import com.omar.myapps.imagetopdf.Adapters.SelectTemplateRAdapter;

import com.omar.myapps.imagetopdf.Model.MyImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class ProcessingActivity extends AppCompatActivity {
    private static final int GALLERY_RC = 1;
    private static final int SHOW_SAVING_RC = 2;


    public int IMAGE_PER_PAGE;
    public String IMAGE_LOCATION_IN_PAGE;
    View edit_image_layout;

    private RecyclerView OpenedImagesRecycleView;
    private OpenedImagesRAdapter recyclerAdapter;
    private List<MyImage> mMyImageUrises;


    public RecyclerView templateRecycleView;
    private SelectTemplateRAdapter recyclerAdapterTemplate;

    private List<MyImage> mMyImageIds;  // this for select template adapter

    public List<Bitmap> bitmapList;

    private List<Uri> uriList;
    LinearLayout parentViewLayout;

    View openImagesLayout, startConvertingLayout, newProjetLayout, savedPdfLayout;
    CropImageView cropImageView;

    public View ImageViewConstraintLayout;

    TextView selectTemplateTV, openImagesTV, startConvertingTV, newProjectOrCloseTV;

    Uri newImageUri;

    private void initRecyclerView() {
        OpenedImagesRecycleView = findViewById(R.id.OpenedImagesRecycleView);
        mMyImageUrises = new ArrayList<>();
        recyclerAdapter = new OpenedImagesRAdapter(ProcessingActivity.this, mMyImageUrises);
        OpenedImagesRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        OpenedImagesRecycleView.setAdapter(recyclerAdapter);
    }

    private void initTemplateRecyclerView() {
        templateRecycleView = findViewById(R.id.templateRecycleView);
        mMyImageIds = new ArrayList<>();

        mMyImageIds.add(new MyImage(R.drawable.one_image_per_page_full));
        mMyImageIds.add(new MyImage(R.drawable.one_image_per_page_top_left));
        mMyImageIds.add(new MyImage(R.drawable.one_image_per_page_top_center));
        mMyImageIds.add(new MyImage(R.drawable.one_image_per_page_top_right));
        mMyImageIds.add(new MyImage(R.drawable.two_image_per_page_horiz));
        mMyImageIds.add(new MyImage(R.drawable.two_image_per_page_vertical_left));
        mMyImageIds.add(new MyImage(R.drawable.two_image_per_page_vertical_center));
        mMyImageIds.add(new MyImage(R.drawable.two_image_per_page_vertical_right));
        mMyImageIds.add(new MyImage(R.drawable.four_images));

        recyclerAdapterTemplate = new SelectTemplateRAdapter(ProcessingActivity.this, mMyImageIds);

        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 3);// each row has 3 colums

        templateRecycleView.setLayoutManager(manager);

        //    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        templateRecycleView.setAdapter(recyclerAdapterTemplate);

        edit_image_layout = findViewById(R.id.edit_image_layout);
    }

    private ImageView imageView, editImagesBTN, savedPdfImageView;

    View selectTemplateLayout;

    public ImageView openImagesBTN, selectNOfImagePerPage, convertToPdfBTN, newProjectOrCloseImageView;


    void init() {
        initRecyclerView();
        initTemplateRecyclerView();

        parentViewLayout = findViewById(R.id.parentViewLayout);
        openImagesLayout = findViewById(R.id.openImagesLayout);

        openImagesBTN = findViewById(R.id.openImageBtn);
        editImagesBTN = findViewById(R.id.EditImagefBTN);
        convertToPdfBTN = findViewById(R.id.convertToPdfBTN);
        newProjectOrCloseImageView = findViewById(R.id.newProjectImageView);
        savedPdfLayout = findViewById(R.id.savedPdfLayout);

        savedPdfImageView = findViewById(R.id.savedPdfImageView);

        selectTemplateTV = findViewById(R.id.selectTemplateTV);
        openImagesTV = findViewById(R.id.openImagesTV);
        startConvertingTV = findViewById(R.id.startConvertingTV);
        newProjectOrCloseTV = findViewById(R.id.newProjectTV);

        selectTemplateLayout = findViewById(R.id.selectTemplateLayout);
        startConvertingLayout = findViewById(R.id.startConvertingLayout);
        newProjetLayout = findViewById(R.id.newProjectLayout);
        editImagesBTN.setVisibility(View.GONE);

        selectNOfImagePerPage = findViewById(R.id.selectTemplateBTN);


        cropImageView = findViewById(R.id.cropImageView);

        uriList = new ArrayList<>();
        bitmapList = new ArrayList<>();

        ImageViewConstraintLayout = findViewById(R.id.constraintLayout);

    }

    ItemTouchHelper.Callback ithCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // get the viewHolder's and target's positions in your adapter data, swap them
            Collections.swap(mMyImageUrises, viewHolder.getAdapterPosition(), target.getAdapterPosition());

            //swap bitmap list to do processing on them
            Collections.swap(bitmapList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

            //swap uri list to do processing on them
            Collections.swap(uriList, viewHolder.getAdapterPosition(), target.getAdapterPosition());


            // and notify the adapter that its data set has changed
            //    recyclerAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            recyclerAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };


    private void updateStuffAfterEditingImage() {
        // updating
        bitmapList.remove(MyImage.currentImageIndex);
        bitmapList.add(MyImage.currentImageIndex, MyImage.workingBitmap);

        mMyImageUrises.remove(MyImage.currentImageIndex);
        mMyImageUrises.add(MyImage.currentImageIndex, new MyImage(MyImage.workingBitmap));
        recyclerAdapter.notifyDataSetChanged();

        newImageUri = createTempBitmapAndGetItsUrl(MyImage.workingBitmap);
        // newImageUri = getUriFromBitmap(getApplicationContext(), MyImage.workingBitmap);
        uriList.remove(MyImage.currentImageIndex);
        uriList.add(MyImage.currentImageIndex, newImageUri);

    }

    private ProgressDialog progDailog;


    public void displayImageToEdit(int imageIndex) {
        imageView.setImageBitmap(bitmapList.get(imageIndex));
        if (editImagesBTN.getVisibility() == View.GONE) { // show edit image button after displaying images
            editImagesBTN.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        init();

        Utils.zoom_in(openImagesLayout, getApplicationContext());

        // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
        ItemTouchHelper ith = new ItemTouchHelper(ithCallback);
        ith.attachToRecyclerView(OpenedImagesRecycleView);


        edit_image_layout.setVisibility(View.GONE);

        editImagesBTN.setEnabled(false);

        imageView = findViewById(R.id.imageView);


        createSavingFloder();

        progDailog = new ProgressDialog(ProcessingActivity.this, R.style.AppCompatAlertDialogStyle);

        openImagesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageFromGallary();
            }
        });

        selectNOfImagePerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNOfImagePerPage.setVisibility(View.GONE);
                templateRecycleView.setVisibility(View.VISIBLE);
                ImageViewConstraintLayout.setVisibility(View.GONE);

                setTemplateRV_FoucusWithScrollView(templateRecycleView);
                Utils.zoom_in(templateRecycleView, ProcessingActivity.this); // animate edit template view
            }
        });


        convertToPdfBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();

                progDailog.setMessage("Processing...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(false);
                progDailog.show();
                myAsyncTask.execute(null, null, null, null);
            }
        });

        editImagesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Utils.zoom_out(edit_image_layout, getApplicationContext());
                        edit_image_layout.setVisibility(View.GONE);
                    }
                }, 5000);
                edit_image_layout.setVisibility(View.VISIBLE);
                setImageFoucusWithScrollView();
                Utils.zoom_in(edit_image_layout, ProcessingActivity.this); // animate edit image layout

            }
        });


        findViewById(R.id.rotateIBNplus90).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.rotateImage(90, imageView);
                updateStuffAfterEditingImage();
            }
        });

        findViewById(R.id.rotateIBNminus90).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.rotateImage(-90, imageView);
                updateStuffAfterEditingImage();
            }
        });

        findViewById(R.id.flipVerticalIBN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.flipImage(Utils.flipVertical, imageView);
                updateStuffAfterEditingImage();
            }
        });

        findViewById(R.id.flipHorizontalIBN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.flipImage(Utils.flipHorizontal, imageView);
                updateStuffAfterEditingImage();
            }
        });

        findViewById(R.id.cropImageIBN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                CropImage.activity(uriList.get(MyImage.currentImageIndex))// put uri here
                        .start(ProcessingActivity.this);
            }
        });

        savedPdfImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProcessingActivity.this, SavingFolderActivity.class);
                intent.putExtra("FolderName", "Converted");
                startActivity(intent);
            }
        });

    }

    private void animateViewHorizantally(View viewTobeAnimated, View rootView) {
        viewTobeAnimated.animate()
                .translationX((rootView.getWidth() - viewTobeAnimated.getWidth()) / 2)
                //   .translationY((root.getHeight() - animatedView.getHeight()) / 2)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(700);
    }

    public void animateViewHorizantallyToView(View viewTobeAnimated, View rootView, View viewToStopAt) {
        viewTobeAnimated.animate()
                .translationX((rootView.getWidth() - viewTobeAnimated.getWidth()) / 2 - viewToStopAt.getWidth() * 2)
                //   .translationY((root.getHeight() - animatedView.getHeight()) / 2)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(500);

    }

    private Uri getUriFromBitmap(Context context, Bitmap ImgBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), ImgBitmap, "Title", null);
        return Uri.parse(path);
    }


    Uri createTempBitmapAndGetItsUrl(Bitmap bitmap) {
        Uri uri = null;
        File file = createTempJPGFile();
        if (file != null) {
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
                uri = Uri.fromFile(file);
                fout.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return uri;
    }

    private File createTempJPGFile() {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String jpgFileName = "temp_jpg_" + timeStamp + "_";

        File jpgFile = null;
        try {
            jpgFile = File.createTempFile(

                    jpgFileName,  // prefix
                    ".jpg",         // suffix
                    tempImgFolder      // directory
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpgFile;
    }


    @Override
    protected void onResume() {

        super.onResume();
        if (Utils.pdfConversionIsDone

                && newProjetLayout.getVisibility() != View.VISIBLE) {
            selectTemplateLayout.setVisibility(View.GONE);
            startConvertingLayout.setVisibility(View.GONE);
            showAndAnimateNewProjectAndShowSavedFileLayout();
        }
    }

    private void showAndAnimateNewProjectAndShowSavedFileLayout() {
        newProjetLayout.setVisibility(View.VISIBLE);
        Utils.zoom_in(newProjetLayout, getApplicationContext());
        savedPdfLayout.setVisibility(View.VISIBLE);
        Utils.zoom_in(savedPdfLayout, getApplicationContext());
        //and hide select and convert layouts
    }

    File savingfolder;
    File tempImgFolder;

    private void createSavingFloder() {
        /**
         * and creating temp folder to save temp images in it after editing images
         * and finally delete them
         **/

        savingfolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "My Pdf" + File.separator + "Converted");

        if (!savingfolder.exists()) {
            savingfolder.mkdirs();
        }


        tempImgFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "My Pdf" + File.separator + "temp");

        if (!tempImgFolder.exists()) {
            tempImgFolder.mkdirs();
        }

    }

    private File createPdfFile() {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("YYYYmmDD").format(new Date());

        String pdfFileName = "PDF_" + timeStamp + "_";

        File pdfFile = null;

        try {
            pdfFile = File.createTempFile(

                    pdfFileName,  // prefix
                    ".pdf",         // suffix
                    savingfolder      // directory
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        //or we can use the line below*/
        //  pdfFile = new File(savingfolder + File.separator + pdfFileName + ".pdf");

        return pdfFile;
    }

    private Bitmap convertUriToBitmap(Uri uri) throws IOException {
        Bitmap newBitmap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            newBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
        } else {
            newBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        }
        return scaleBitmapAsAspectRatio(newBitmap);
    }

    private void convertImageToPdf(int imagePerPage, String IMAGE_LOCATION_IN_PAGE) {

        if (imagePerPage == 1 && IMAGE_LOCATION_IN_PAGE.equals("fit_page")) {
            convertOneImagePerPage("fit_page");
        } else if (imagePerPage == 1 && IMAGE_LOCATION_IN_PAGE.equals("Top_Left")) {
            convertOneImagePerPage("Top_Left");
        } else if (imagePerPage == 1 && IMAGE_LOCATION_IN_PAGE.equals("Top_Center")) {
            convertOneImagePerPage("Top_Center");
        } else if (imagePerPage == 1 && IMAGE_LOCATION_IN_PAGE.equals("Top_Right")) {
            convertOneImagePerPage("Top_Right");
        } else if (imagePerPage == 1 && IMAGE_LOCATION_IN_PAGE.equals("Mid_Center")) {
            convertOneImagePerPage("Mid_Center");
        } else if (imagePerPage == 2 && IMAGE_LOCATION_IN_PAGE.equals("Horizontal")) {
            convertImagesToPdf_2_IMAGE_PER_PAGE("Horizontal");
        } else if (imagePerPage == 2 && IMAGE_LOCATION_IN_PAGE.equals("Vertical_Left")) {
            convertImagesToPdf_2_IMAGE_PER_PAGE(IMAGE_LOCATION_IN_PAGE);
        } else if (imagePerPage == 2 && IMAGE_LOCATION_IN_PAGE.equals("Vertical_Center")) {
            convertImagesToPdf_2_IMAGE_PER_PAGE(IMAGE_LOCATION_IN_PAGE);
        } else if (imagePerPage == 2 && IMAGE_LOCATION_IN_PAGE.equals("Vertical_Right")) {
            convertImagesToPdf_2_IMAGE_PER_PAGE(IMAGE_LOCATION_IN_PAGE);
        } else if (imagePerPage == 4) {
            convertImagesToPdf_4_IMAGE_PER_PAGE();
        }
    }

    private void convertOneImagePerPage(String IMAGE_LOCATION_IN_PAGE) {

        if (IMAGE_LOCATION_IN_PAGE.equals("fit_page")) {
            try {

                Document document = new Document(PageSize.A4);
                File myPDFFile = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDFFile));

                PdfPCell pdfPCell;
                document.open();
                PdfPTable table;
                for (int i = 0; i < bitmapList.size(); i++) {

                    table = new PdfPTable(1);

                    table.setTotalWidth(PageSize.A4.getWidth());  //table width same as page width
                    table.setLockedWidth(true);

                    byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i));
                    Image myImage = Image.getInstance(byteArray);
                    myImage.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());

                    pdfPCell = new PdfPCell(myImage, true);
                    pdfPCell.setBorder(Rectangle.NO_BORDER);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                    table.addCell(pdfPCell);

                    document.newPage();
                    document.add(table);

                }
                document.close();
            } catch (IOException ioE) {
                ioE.printStackTrace();
            } catch (BadElementException beE) {
                beE.printStackTrace();
            } catch (DocumentException dE) {
                dE.printStackTrace();
            }
        } else if (IMAGE_LOCATION_IN_PAGE.equals("Top_Left")) {
            try {

                Document document = new Document(PageSize.A4);
                File myPDFFile = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDFFile));

                PdfPCell pdfPCell;
                document.open();
                PdfPTable table;
                for (int i = 0; i < bitmapList.size(); i++) {

                    table = new PdfPTable(1);

                    table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                    table.setLockedWidth(true);

                    byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i));
                    Image myImage = Image.getInstance(byteArray);
                    myImage.scaleToFit(PageSize.A4.getWidth() / 2, (PageSize.A4.getHeight() / 2) - document.topMargin());


                    pdfPCell = new PdfPCell(myImage, true);
                    pdfPCell.setBorder(Rectangle.NO_BORDER);
                    pdfPCell.setCalculatedHeight((PageSize.A4.getHeight() / 2) - document.topMargin());


                    table.addCell(pdfPCell);
                    table.setHorizontalAlignment(Element.ALIGN_LEFT);// top left

                    document.newPage();
                    document.add(table);

                }
                document.close();
            } catch (IOException ioE) {
                ioE.printStackTrace();
            } catch (BadElementException beE) {
                beE.printStackTrace();
            } catch (DocumentException dE) {
                dE.printStackTrace();
            }
        } else if (IMAGE_LOCATION_IN_PAGE.equals("Top_Center")) {
            try {

                Document document = new Document(PageSize.A4);
                File myPDFFile = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDFFile));

                PdfPCell pdfPCell;
                document.open();
                PdfPTable table;
                for (int i = 0; i < bitmapList.size(); i++) {

                    table = new PdfPTable(1);
                    table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                    table.setLockedWidth(true);

                    byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i));
                    Image myImage = Image.getInstance(byteArray);
                    myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                    pdfPCell = new PdfPCell(myImage, true);
                    pdfPCell.setBorder(Rectangle.NO_BORDER);
                    pdfPCell.setPadding(5f);
                    pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_TOP | Element.ALIGN_CENTER);


                    table.addCell(pdfPCell);

                    document.newPage();
                    document.add(table);

                }
                document.close();
            } catch (IOException ioE) {
                ioE.printStackTrace();
            } catch (BadElementException beE) {
                beE.printStackTrace();
            } catch (DocumentException dE) {
                dE.printStackTrace();
            }
        } else if (IMAGE_LOCATION_IN_PAGE.equals("Top_Right")) {
            try {

                Document document = new Document(PageSize.A4);
                File myPDFFile = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDFFile));

                PdfPCell pdfPCell;
                document.open();
                PdfPTable table;
                for (int i = 0; i < bitmapList.size(); i++) {

                    table = new PdfPTable(1);

                    table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                    table.setLockedWidth(true);

                    byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i));
                    Image myImage = Image.getInstance(byteArray);
                    myImage.scaleToFit(PageSize.A4.getWidth() / 2, (PageSize.A4.getHeight() / 2) - document.topMargin());


                    pdfPCell = new PdfPCell(myImage, true);
                    pdfPCell.setBorder(Rectangle.NO_BORDER);
                    pdfPCell.setCalculatedHeight((PageSize.A4.getHeight() / 2) - document.topMargin());


                    table.addCell(pdfPCell);
                    table.setHorizontalAlignment(Element.ALIGN_RIGHT);// top right


                    document.newPage();
                    document.add(table);

                }
                document.close();
            } catch (IOException ioE) {
                ioE.printStackTrace();
            } catch (BadElementException beE) {
                beE.printStackTrace();
            } catch (DocumentException dE) {
                dE.printStackTrace();
            }
        }

    }

    private void convertImagesToPdf_2_IMAGE_PER_PAGE(String IMAGE_LOCATION_IN_PAGE) {

        if (IMAGE_LOCATION_IN_PAGE.equals("Horizontal")) {
            try {
                Document document = new Document(PageSize.A4);
                File myPDF_File = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDF_File));

                document.open();
                PdfPTable table = null;
                PdfPCell pdfPCell;
                for (int i = 1; i <= bitmapList.size(); i++) {
                    if (i % 2 == 0) {

                        /** insert 2 cells into table, each cell contains one image
                         *                         i = 2    ==>    ImageIndex = 1,0
                         *                         i = 4,   ==>    ImageIndex = 3,2
                         **/

                        table = new PdfPTable(2);

                        table.setTotalWidth(PageSize.A4.getWidth());  //table width same as page width
                        table.setLockedWidth(true);

                        int index = i;
                        for (int j = 0; j < 2; j++) {
                            byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(--index));
                            Image myImage = Image.getInstance(byteArray);
                            // image scale=colum height
                            myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                            pdfPCell = new PdfPCell(myImage, true);
                            pdfPCell.setBorder(Rectangle.NO_BORDER);
                            pdfPCell.setPadding(5f);
                            pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                            table.addCell(pdfPCell);
                        }
                        document.setMargins(2, 2, 0, 0);
                        document.newPage();
                        document.add(table);
                    } // adding last image into new table with 1 col, if the number of images was odd
                    else if (i == bitmapList.size()) {
                        table = new PdfPTable(1);

                        table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                        table.setLockedWidth(true);

                        byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i - 1));
                        Image myImage = Image.getInstance(byteArray);
                        myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                        pdfPCell = new PdfPCell(myImage, true);
                        pdfPCell.setBorder(Rectangle.NO_BORDER);
                        pdfPCell.setPadding(5f);
                        pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                        table.addCell(pdfPCell);
                        document.newPage();
                        document.add(table);
                    }
                }
                document.close();
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        } // end horizantoal

        else if (IMAGE_LOCATION_IN_PAGE.equals("Vertical_Left")) {
            try {
                Document document = new Document(PageSize.A4);
                File myPDF_File = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDF_File));

                document.open();
                PdfPTable table = null;
                PdfPCell pdfPCell;
                for (int i = 1; i <= bitmapList.size(); i++) {
                    if (i % 2 == 0) {

                        /** insert 2 cells into table, each cell contains one image
                         *                         i = 2    ==>    ImageIndex = 1,0
                         *                         i = 4,   ==>    ImageIndex = 3,2
                         **/

                        table = new PdfPTable(1);

                        table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                        table.setLockedWidth(true);
                        table.setHorizontalAlignment(Element.ALIGN_LEFT);

                        int index = i;
                        for (int j = 0; j < 2; j++) {
                            byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(--index));
                            Image myImage = Image.getInstance(byteArray);
                            // image scale=colum height
                            myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                            pdfPCell = new PdfPCell(myImage, true);
                            pdfPCell.setBorder(Rectangle.NO_BORDER);
                            pdfPCell.setPadding(5f);
                            pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                            table.addCell(pdfPCell);
                        }

                        document.newPage();
                        document.add(table);
                    } // adding last image into new table with 1 col, if the number of images was odd
                    else if (i == bitmapList.size()) {
                        table = new PdfPTable(1);
                        table.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                        table.setLockedWidth(true);

                        byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i - 1));
                        Image myImage = Image.getInstance(byteArray);
                        myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                        pdfPCell = new PdfPCell(myImage, true);
                        pdfPCell.setBorder(Rectangle.NO_BORDER);
                        pdfPCell.setPadding(5f);
                        pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                        table.addCell(pdfPCell);

                        document.newPage();
                        document.add(table);
                    }
                }
                document.close();
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        } else if (IMAGE_LOCATION_IN_PAGE.equals("Vertical_Center")) {
            try {
                Document document = new Document(PageSize.A4);
                File myPDF_File = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDF_File));

                document.open();
                PdfPTable table = null;
                PdfPCell pdfPCell;
                for (int i = 1; i <= bitmapList.size(); i++) {
                    if (i % 2 == 0) {

                        /** insert 2 cells into table, each cell contains one image
                         *                         i = 2    ==>    ImageIndex = 1,0
                         *                         i = 4,   ==>    ImageIndex = 3,2
                         **/

                        table = new PdfPTable(1);

                        table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                        table.setLockedWidth(true);
                        table.setHorizontalAlignment(Element.ALIGN_CENTER);

                        int index = i;
                        for (int j = 0; j < 2; j++) {
                            byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(--index));
                            Image myImage = Image.getInstance(byteArray);
                            // image scale=colum height
                            myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                            pdfPCell = new PdfPCell(myImage, true);
                            pdfPCell.setBorder(Rectangle.NO_BORDER);
                            pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                            table.addCell(pdfPCell);
                        }

                        document.newPage();
                        document.add(table);
                    } // adding last image into new table with 1 col, if the number of images was odd
                    else if (i == bitmapList.size()) {
                        table = new PdfPTable(1);
                        table.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                        table.setLockedWidth(true);

                        byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i - 1));
                        Image myImage = Image.getInstance(byteArray);
                        myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                        pdfPCell = new PdfPCell(myImage, true);
                        pdfPCell.setBorder(Rectangle.NO_BORDER);
                        pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                        table.addCell(pdfPCell);

                        document.newPage();
                        document.add(table);
                    }
                }
                document.close();
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        } else if (IMAGE_LOCATION_IN_PAGE.equals("Vertical_Right")) {
            try {
                Document document = new Document(PageSize.A4);
                File myPDF_File = createPdfFile();
                PdfWriter.getInstance(document, new FileOutputStream(myPDF_File));

                document.open();
                PdfPTable table = null;
                PdfPCell pdfPCell;
                for (int i = 1; i <= bitmapList.size(); i++) {
                    if (i % 2 == 0) {

                        /** insert 2 cells into table, each cell contains one image
                         *                         i = 2    ==>    ImageIndex = 1,0
                         *                         i = 4,   ==>    ImageIndex = 3,2
                         **/

                        table = new PdfPTable(1);

                        table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                        table.setLockedWidth(true);
                        table.setHorizontalAlignment(Element.ALIGN_RIGHT);

                        int index = i;
                        for (int j = 0; j < 2; j++) {
                            byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(--index));
                            Image myImage = Image.getInstance(byteArray);
                            // image scale=colum height
                            myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                            pdfPCell = new PdfPCell(myImage, true);
                            pdfPCell.setBorder(Rectangle.NO_BORDER);
                            pdfPCell.setPadding(5f);
                            pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                            table.addCell(pdfPCell);
                        }

                        document.newPage();
                        document.add(table);
                    } // adding last image into new table with 1 col, if the number of images was odd
                    else if (i == bitmapList.size()) {
                        table = new PdfPTable(1);
                        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.setTotalWidth(PageSize.A4.getWidth() / 2);  //table width same as page width
                        table.setLockedWidth(true);

                        byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i - 1));
                        Image myImage = Image.getInstance(byteArray);
                        myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                        pdfPCell = new PdfPCell(myImage, true);
                        pdfPCell.setBorder(Rectangle.NO_BORDER);
                        pdfPCell.setPadding(5f);
                        pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                        table.addCell(pdfPCell);

                        document.newPage();
                        document.add(table);
                    }
                }
                document.close();
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertImagesToPdf_4_IMAGE_PER_PAGE() {

        try {
            Document document = new Document();
            File myPDFFile = createPdfFile();
            PdfWriter.getInstance(document, new FileOutputStream(myPDFFile));

            document.open();
            PdfPTable table = null;// = new com.itextpdf.text.pdf.PdfPTable(2);
            PdfPCell pdfPCell;
            for (int i = 1; i <= bitmapList.size(); i++) {

                if (i % 4 == 0) {
                    table = new PdfPTable(2);

                    table.setHorizontalAlignment(Element.ALIGN_CENTER);

                    int index = i;
                    for (int j = 0; j < 4; j++) {

                        byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(--index));
                        Image myImage = Image.getInstance(byteArray);
                        // image scale=colum height
                        myImage.scaleToFit(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2);

                        pdfPCell = new PdfPCell(myImage, true);
                        pdfPCell.setPadding(5f);
                        pdfPCell.setBorder(Rectangle.NO_BORDER);
                        pdfPCell.setCalculatedHeight(PageSize.A4.getHeight() / 2);
                        table.addCell(pdfPCell);

                    }
                    document.newPage();
                    document.add(table);
                } else if (i == bitmapList.size()) {  /// adding last image the the number of images was odd
                    table = new PdfPTable(1);
                    byte[] byteArray = convertBitmapToArrayOfByte(bitmapList.get(i - 1));
                    Image myImage = Image.getInstance(byteArray);

                    table.addCell(new PdfPCell(myImage, true));
                    document.newPage();
                    document.add(table);
                }
            }

            document.close();
        } catch (IOException ioE) {
            ioE.printStackTrace();

        } catch (BadElementException beE) {
            beE.printStackTrace();
        } catch (DocumentException dE) {
            dE.printStackTrace();
        }

    }

    public void PickImageFromGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_RC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            Bitmap newBitmap;
            // When an Image is picked
            if (requestCode == GALLERY_RC && resultCode == RESULT_OK) {
                // Get the Image from data one image

                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    uriList.add(mImageUri);

                    newBitmap = convertUriToBitmap(mImageUri);

                    bitmapList.add(newBitmap);

                    mMyImageUrises.add(new MyImage(newBitmap));
                    recyclerAdapter.notifyDataSetChanged();
                    displayImageToEdit(0); // after opening the images select first image to display

                    //   openImagesBTN.setVisibility(View.GONE);

                    openImagesLayout.setVisibility(View.GONE);
                    editImagesBTN.setEnabled(true);
                    //   selectNOfImagePerPage.setEnabled(true);


                    showAndAnimateSelectTemplateLayout();

                } else {
                    if (data.getClipData() != null) { // multi selection
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri mImageUri = item.getUri();

                            uriList.add(mImageUri);

                            newBitmap = convertUriToBitmap(mImageUri);

                            bitmapList.add(newBitmap);
                            mMyImageUrises.add(new MyImage(newBitmap));
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        displayImageToEdit(0); // after opening the images select first image to display

                        openImagesBTN.setVisibility(View.GONE);

                        editImagesBTN.setEnabled(true);

                        showAndAnimateSelectTemplateLayout();

                    }
                }
            } else if (requestCode == SHOW_SAVING_RC) {// show saving activity dir

                Toast.makeText(this, "file must be shown", Toast.LENGTH_LONG).show();
            }
            // when cropping image
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    Uri resultUri = result.getUri();   // get and display cropped image
                    imageView.setImageURI(resultUri);


                    newBitmap = convertUriToBitmap(resultUri);

                    // here make new uri // create new image(cropped) and take its url to process again on it
                    newImageUri = createTempBitmapAndGetItsUrl(newBitmap);
                    updateStuffAfterCroppingImage(newImageUri, newBitmap, MyImage.currentImageIndex);

                    imageView.setVisibility(View.VISIBLE);
                    cropImageView.setVisibility(View.GONE);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "RESULT_CANCELED", Toast.LENGTH_SHORT).show();
                    // imageView.setImageURI(resultUri);
                    imageView.setVisibility(View.VISIBLE);
                    cropImageView.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateStuffAfterCroppingImage(Uri resultUri, Bitmap newBitmap, byte currentImageIndex) {
        // updating after crop image
        uriList.remove(currentImageIndex);
        uriList.add(currentImageIndex, resultUri);

        bitmapList.remove(currentImageIndex);
        bitmapList.add(currentImageIndex, newBitmap);

        mMyImageUrises.remove(currentImageIndex);   // to update recycler view
        mMyImageUrises.add(currentImageIndex, new MyImage(newBitmap));
        recyclerAdapter.notifyDataSetChanged();
    }

    private void showAndAnimateSelectTemplateLayout() {
        selectTemplateLayout.setVisibility(View.VISIBLE);
        Utils.zoom_in(selectTemplateLayout, getApplicationContext());
    }

    public byte[] convertBitmapToArrayOfByte(Bitmap bitmap) {
        //convert bitmap to array of bytes (Image)
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    class MyAsyncTask extends AsyncTask<Object, String, Object> {

        @Override
        protected Object doInBackground(Object... params) {

            convertImageToPdf(IMAGE_PER_PAGE, IMAGE_LOCATION_IN_PAGE);

            return params;
        }

        @Override
        protected void onPostExecute(Object result) {
            progDailog.dismiss();
            Toast.makeText(ProcessingActivity.this, "done", Toast.LENGTH_SHORT).show();
            showSavedFiles();
            Utils.pdfConversionIsDone = true;
            finishingProject();
        }
    }


    private void showSavedFiles() {
        Intent intent = new Intent(ProcessingActivity.this, SavingFolderActivity.class);
        intent.putExtra("FolderName", "Converted");
        startActivity(intent);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private Bitmap scaleBitmapAsAspectRatio(Bitmap bitmapImage) {
        float scaledVal;
        if (bitmapImage.getHeight() > 1024 && bitmapImage.getWidth() > 1024) {
            scaledVal = 750.0f;
            int nh = (int) (bitmapImage.getHeight() * (scaledVal / bitmapImage.getWidth()));
            return Bitmap.createScaledBitmap(bitmapImage, (int) scaledVal, nh, true);
        } else if (bitmapImage.getHeight() < 1024 && bitmapImage.getHeight() > 512) {
            scaledVal = 512.0f;
            int nh = (int) (bitmapImage.getHeight() * (scaledVal / bitmapImage.getWidth()));
            return Bitmap.createScaledBitmap(bitmapImage, (int) scaledVal, nh, true);
        } else {
            // return bitmap as its
            return bitmapImage;
        }
    }

    public void showAndAnimateStartConvertingLayout() {
        startConvertingLayout.setVisibility(View.VISIBLE);
        Utils.zoom_in(startConvertingLayout, getApplicationContext());
        selectTemplateTV.setTextColor(Color.DKGRAY);
    }

    private void finishingProject() {
        deleteTempImages();
    }

    private void deleteTempImages() {
        try {
            File fdelete = new File(tempImgFolder.toURI());
            if (fdelete.exists()) {
                for (File file : fdelete.listFiles()) {

                    if (file.delete()) {
                        Log.d("deleting image Uri", "file was Deleted !");
                    } else {
                        Log.d("deleting image Uri", "file was not Deleted !");
                    }
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }


    }

    public void setImageFoucusWithScrollView() {
        View targetView = findViewById(R.id.constraintLayout);
        targetView.getParent().requestChildFocus(targetView, targetView);
    }

    private void setTemplateRV_FoucusWithScrollView(View targetView) {
        targetView.getParent().requestChildFocus(targetView, targetView);
    }


}
