package com.example.File_Retrieval_Engine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

    public String getFileName() {
        return fileName;
    }

    public Long getMinSize() {
        return minSize;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public String getContentSearchTerm() {
        return contentSearchTerm;
    }

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public String getFileType() {
        return fileType;
    }
}
