package com.omar.myapps.imagetopdf;

import android.graphics.Bitmap;
import android.net.Uri;

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

    public  void setImageID(int imageID) {
        ImageID = imageID;
    }


    public Bitmap bitmap;

    public Uri getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }

    public MyImage(Uri imgUri) {
        this.imgUri = imgUri;
    }

    public Uri imgUri;

    public MyImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


}
