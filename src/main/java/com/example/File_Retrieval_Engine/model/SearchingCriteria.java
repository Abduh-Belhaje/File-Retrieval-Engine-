package com.example.File_Retrieval_Engine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Represents the search criteria for searching files within the file retrieval engine.
 * It contains various filters like file name, size, content, creation date, and file type.
 */
@Getter
@Setter
public class SearchingCriteria {

    private String fileName;
    private Long minSize;
    private Long maxSize;
    private String contentSearchTerm;
    private Date createdAfter;
    private Date createdBefore;
    private String fileType;

    /**
     * Gets the file name that is being searched for.
     *
     * @return The name of the file to search for.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the minimum size of the file to be searched for.
     *
     * @return The minimum size of the file in bytes.
     */
    public Long getMinSize() {
        return minSize;
    }

    /**
     * Gets the maximum size of the file to be searched for.
     *
     * @return The maximum size of the file in bytes.
     */
    public Long getMaxSize() {
        return maxSize;
    }

    /**
     * Gets the content search term to filter files by their content.
     *
     * @return The content search term to be used for filtering.
     */
    public String getContentSearchTerm() {
        return contentSearchTerm;
    }

    /**
     * Gets the date after which the files must have been created.
     *
     * @return The date after which files should be created.
     */
    public Date getCreatedAfter() {
        return createdAfter;
    }

    /**
     * Gets the date before which the files must have been created.
     *
     * @return The date before which files should be created.
     */
    public Date getCreatedBefore() {
        return createdBefore;
    }

    /**
     * Gets the file type (extension) to filter files by their type.
     *
     * @return The file type to filter, e.g., "txt", "jpg".
     */
    public String getFileType() {
        return fileType;
    }
}
