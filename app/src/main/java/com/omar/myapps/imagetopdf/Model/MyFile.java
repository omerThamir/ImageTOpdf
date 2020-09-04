package com.omar.myapps.imagetopdf.Model;

import android.net.Uri;

public class MyFile {
    private String name;
    private String full_path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public MyFile(String name, String full_path) {
        this.name = name;
        this.full_path = full_path;
    }

    private Uri uri;

    public MyFile(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

}
