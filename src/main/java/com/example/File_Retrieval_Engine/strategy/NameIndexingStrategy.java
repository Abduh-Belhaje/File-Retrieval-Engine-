package com.example.File_Retrieval_Engine.strategy;

import com.example.File_Retrieval_Engine.model.FileInfo;
import com.example.File_Retrieval_Engine.service.IndexStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NameIndexingStrategy implements IndexingStrategy{

    private static final Logger logger = LoggerFactory.getLogger(NameIndexingStrategy.class);

    @Override
    public void indexFile(File file, FileInfo fileInfo) {

        try {
            Map<String, List<FileInfo>> localIndex = new TreeMap<>();

            // If the size is not in the local index, add it with an empty ArrayList
            localIndex.putIfAbsent(fileInfo.getName(), new ArrayList<>());

            // Get the fileInfo List for this size
            List<FileInfo> fileInfoList = localIndex.get(fileInfo.getName());
            fileInfoList.add(fileInfo);

            localIndex.put(fileInfo.getName(), fileInfoList);

            mergeWithGlobalIndex(localIndex);
        } catch (Exception e) {
            logger.warn("Error processing file {}: {}", file.getName(), e.getMessage());
        }

    }

    private void mergeWithGlobalIndex(Map<String, List<FileInfo>> localIndex){
        IndexStore.getInstance().updateNameIndex(localIndex);
    }
}
