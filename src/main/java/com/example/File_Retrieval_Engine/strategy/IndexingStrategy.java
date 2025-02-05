package com.example.File_Retrieval_Engine.strategy;

import com.example.File_Retrieval_Engine.model.FileInfo;

import java.io.File;

public interface IndexingStrategy {
    void indexFile(File file, FileInfo fileInfo);
}
