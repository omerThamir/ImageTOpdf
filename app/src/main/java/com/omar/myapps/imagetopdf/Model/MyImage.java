package com.omar.myapps.imagetopdf.Model;

import android.graphics.Bitmap;

public class MyImage {

    public static boolean isImageProcessed;
    public static byte currentImageIndex;
    public static Bitmap workingBitmap;

    private int ImageID; // for template recycler adapter

    public MyImage(int imageID) {
        this.ImageID = imageID;
    }

    public int getImageID() {
        return ImageID;
    }


    public Bitmap bitmap;

    public MyImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
