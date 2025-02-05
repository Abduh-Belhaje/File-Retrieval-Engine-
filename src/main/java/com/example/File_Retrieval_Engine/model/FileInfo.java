package com.example.File_Retrieval_Engine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileInfo{

    private String name;
    private String path;
    private long size;
    private long last_modification;
    private String type;

    public FileInfo(String name, String path, long size, long lastModified) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.last_modification = lastModified;
        this.type = extractType();
    }

    private String extractType(){
        String[] params = name.split("\\.");
        return params[params.length - 1];
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public long getLast_modification() {
        return last_modification;
    }

    public String getType() {
        return type;
    }
}
