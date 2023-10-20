package com.gumdom.boot.infrastructure.Entity;

public class FileObject {

    private String name;

    private String path;


    public FileObject() {
    }

    public FileObject(String name, String path) {
        this.name = name;
        this.path = path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public String toString() {
        return "FileObject{" +
                "name='" + name + '\'' +
                ", nate='" + path + '\'' +
                '}';
    }
}
