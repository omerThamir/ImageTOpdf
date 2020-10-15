package com.omar.myapps.imagetopdf;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.omar.myapps.imagetopdf.Model.MyImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    public static boolean pdfConversionIsDone = false;
    public static boolean pdfMergingIsDone=false;
    private static Bitmap processingBitmap;
    public static float[] flipVertical = {1.0f, -1.0f};
    public static float[] flipHorizontal = {-1.0f, 1.0f};

    public static void clearProcessingBitmap() {
        if (processingBitmap != null) processingBitmap.recycle();
    }


    public static void flipImage(float flipType[], ImageView editImageView) {
        if (processingBitmap != null) processingBitmap = null;
        // the 3 lines below used to get bitmap from image view
        editImageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) editImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Matrix matrix = new Matrix();
        matrix.preScale(flipType[0], flipType[1]);

        processingBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);

        editImageView.setImageBitmap(processingBitmap);
        MyImage.workingBitmap = Bitmap.createBitmap(processingBitmap, 0, 0, processingBitmap.getWidth(), processingBitmap.getHeight());
    }

    public static void zoom_in(View v, Context c) {
        Animation animation = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        v.startAnimation(animation);
        if(c instanceof ProcessingActivity){
            ((ProcessingActivity) c).animateViewHorizantallyToView(((ProcessingActivity) c).openImagesTV,((ProcessingActivity) c).parentViewLayout,((ProcessingActivity) c).openImagesBTN);
        }
    }

    public static void zoom_out(View v, Context c) {
        Animation animation = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        v.startAnimation(animation);
    }

    public static void DoLeftToRightAnimation(View v, Context c) {
        Animation animation = AnimationUtils.loadAnimation(c, R.anim.left_to_right);
        v.startAnimation(animation);
    }

    public static void rotateImage(float degree, ImageView editImageView) {
        if (processingBitmap != null) processingBitmap = null;
        // the 3 lines below used to get bitmap from image view
        editImageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) editImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        processingBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);

        editImageView.setImageBitmap(processingBitmap);
        MyImage.workingBitmap = Bitmap.createBitmap(processingBitmap, 0, 0, processingBitmap.getWidth(), processingBitmap.getHeight());

    }


    public String compressImage(Context context, String imageUri) {

        String filePath = getRealPathFromURI(context, imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(Context context, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }





    public static void AnimateFromLeftToCenter(View viewTobeAnimated, LinearLayout ParentView, Context context) {


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        ObjectAnimator translationX = ObjectAnimator.ofFloat(viewTobeAnimated,
                "x",
                ((metrics.widthPixels / 2) - viewTobeAnimated.getWidth() / 2)); // 55 view width
        translationX.setDuration(1000);
        translationX.start();

        Toast.makeText(context, "" + (viewTobeAnimated.getWidth()), Toast.LENGTH_SHORT).show();

/*
        int layoutWidth = ParentView.getWidth();

        layoutWidth = ((int) layoutWidth / 2) - (viewTobeAnimated.getWidth());

        viewTobeAnimated.animate()
                .translationX(-layoutWidth)
                .translationY(0)
                .setDuration(500)
                .setInterpolator(new LinearInterpolator())
                .start();



 */
        float parentCenterX = (ParentView.getX() + ParentView.getWidth());
        //   float parentCenterY = ParentView.getY() + ParentView.getHeight() / 2;

      /*  viewTobeAnimated.animate().
                translationX(parentCenterX - viewTobeAnimated.getWidth() / 2 -viewTobeAnimated.getX());
        // .translationY(parentCenterY - viewTobeAnimated.getHeight() / 2);


       */

      /*  ObjectAnimator animation = ObjectAnimator.ofFloat(viewTobeAnimated, "translationX",
                200);
        animation.setDuration(2000);
        animation.start();

       */
        //   Toast.makeText(context, "" + (layoutWidth), Toast.LENGTH_SHORT).show();

    }

}





