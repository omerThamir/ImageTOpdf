package com.omar.myapps.imagetopdf;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class Utils {
    private static Bitmap processingBitmap;
    public static float[] flipVertical = {1.0f, -1.0f};
    public static float[] flipHorizontal = {-1.0f, 1.0f};


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

}





