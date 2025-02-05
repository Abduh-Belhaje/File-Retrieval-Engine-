package com.example.File_Retrieval_Engine.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a file with information such as its name, path, size, last modification date, and type.
 * This class is used for storing and retrieving file metadata.
 */
@Getter
@Setter
public class FileInfo {

    private String name;
    private String path;
    private long size;
    private long last_modification;
    private String type;

    /**
     * Constructs a new FileInfo object with the specified file details.
     *
     * @param name            The name of the file.
     * @param path            The path of the file.
     * @param size            The size of the file in bytes.
     * @param lastModified    The last modification timestamp of the file.
     */
    public FileInfo(String name, String path, long size, long lastModified) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.last_modification = lastModified;
        this.type = extractType();
    }

    /**
     * Extracts the file type based on the file's name by splitting the name using the period ('.') as a delimiter.
     * Assumes that the file name contains an extension.
     *
     * @return The file type (extension), e.g., "txt", "jpg".
     */
    private String extractType() {
        String[] params = name.split("\\.");
        return params[params.length - 1];
    }

    /**
     * Gets the name of the file.
     *
     * @return The name of the file.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the path of the file.
     *
     * @return The path of the file.
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the size of the file.
     *
     * @return The size of the file in bytes.
     */
    public long getSize() {
        return size;
    }

    /**
     * Gets the last modification timestamp of the file.
     *
     * @return The last modification time of the file.
     */
    public long getLast_modification() {
        return last_modification;
    }

    /**
     * Gets the type (extension) of the file.
     *
     * @return The file type, e.g., "txt", "jpg".
     */
    public String getType() {
        return type;
    }
}
