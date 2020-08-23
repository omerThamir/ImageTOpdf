package com.omar.myapps.imagetopdf;

import android.net.Uri;

public class MyImage {
    public Uri imageUri;

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
