package com.omar.myapps.imagetopdf;

public class File {
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

    public File(String name, String full_path) {
        this.name = name;
        this.full_path = full_path;
    }
}
