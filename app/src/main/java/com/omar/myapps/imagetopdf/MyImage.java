package com.omar.myapps.imagetopdf;

import android.graphics.Bitmap;
import android.net.Uri;

public class MyImage {

    public static boolean isImageProcessed;
    public static byte currentImageIndex;
    public static Bitmap workingBitmap;
    private Uri imageUri;

    public static Bitmap bitmap;

    public MyImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static void setBitmap(Bitmap bitmap) {
        MyImage.bitmap = bitmap;
    }

    public MyImage(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
