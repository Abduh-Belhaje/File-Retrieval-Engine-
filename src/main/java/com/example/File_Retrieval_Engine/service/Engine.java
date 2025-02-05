package com.example.File_Retrieval_Engine.service;

import com.example.File_Retrieval_Engine.exception.EmptyDataSetException;
import com.example.File_Retrieval_Engine.model.FileInfo;
import com.example.File_Retrieval_Engine.model.SearchingCriteria;

import java.util.List;

public interface Engine {

    void index(String path) throws EmptyDataSetException;

    List<FileInfo> search(SearchingCriteria criteria);
}
