package com.omar.myapps.imagetopdf;

import android.graphics.Bitmap;
import android.net.Uri;

public class Image {

    public static boolean isImageProcessed;
    public static byte currentImageIndex;
    public static Bitmap workingBitmap;

    public Bitmap bitmap;

    public Image(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
