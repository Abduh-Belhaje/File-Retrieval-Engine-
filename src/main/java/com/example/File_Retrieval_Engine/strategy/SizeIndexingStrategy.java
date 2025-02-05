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

/**
 * Strategy for indexing files based on their size.
 * This class implements the IndexingStrategy interface and organizes files in an index based on their size.
 */
public class SizeIndexingStrategy implements IndexingStrategy{

    private static final Logger logger = LoggerFactory.getLogger(SizeIndexingStrategy.class);

    /**
     * Indexes a file based on its size.
     *
     * @param file     the file to be indexed
     * @param fileInfo metadata about the file including its size
     */
    @Override
    public void indexFile(File file, FileInfo fileInfo) {
        try {
            Map<Long, List<FileInfo>> localIndex = new TreeMap<>();

            // If the size is not in the local index, add it with an empty ArrayList
            localIndex.putIfAbsent(fileInfo.getSize(), new ArrayList<>());

            // Retrieve the fileInfo list associated with this size
            List<FileInfo> fileInfoList = localIndex.get(fileInfo.getSize());
            fileInfoList.add(fileInfo);

            // Update the local index
            localIndex.put(fileInfo.getSize(), fileInfoList);

            // Merge the local index with the global index
            mergeWithGlobalIndex(localIndex);
        } catch (Exception e) {
            logger.warn("Error processing file {}: {}", file.getName(), e.getMessage());
        }
    }

    /**
     * Merges the local index with the global size index stored in IndexStore.
     *
     * @param localIndex the local size index to be merged
     */
    private void mergeWithGlobalIndex(Map<Long, List<FileInfo>> localIndex){
        IndexStore.getInstance().updateSizeIndex(localIndex);
    }
}
