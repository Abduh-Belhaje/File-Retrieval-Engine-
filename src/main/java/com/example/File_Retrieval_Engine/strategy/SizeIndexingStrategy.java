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

public class SizeIndexingStrategy implements IndexingStrategy{

    private static final Logger logger = LoggerFactory.getLogger(SizeIndexingStrategy.class);

    @Override
    public void indexFile(File file, FileInfo fileInfo) {

        try {
            Map<Long, List<FileInfo>> localIndex = new TreeMap<>();

            // If the size is not in the local index, add it with an empty ArrayList
            localIndex.putIfAbsent(fileInfo.getSize(), new ArrayList<>());

            // Get the fileInfo List for this size
            List<FileInfo> fileInfoList = localIndex.get(fileInfo.getSize());
            fileInfoList.add(fileInfo);

            localIndex.put(fileInfo.getSize(), fileInfoList);

            mergeWithGlobalIndex(localIndex);
        } catch (Exception e) {
            logger.warn("Error processing file {}: {}", file.getName(), e.getMessage());
        }

    }

    private void mergeWithGlobalIndex(Map<Long, List<FileInfo>> localIndex){
        IndexStore.getInstance().updateSizeIndex(localIndex);
    }
}
