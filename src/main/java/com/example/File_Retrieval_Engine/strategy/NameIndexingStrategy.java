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
 * Strategy for indexing files based on their names.
 * This class implements the {@link IndexingStrategy} interface
 * and stores file information in a global name-based index.
 */
public class NameIndexingStrategy implements IndexingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(NameIndexingStrategy.class);

    /**
     * Indexes a file based on its name.
     * This method extracts the file name from the {@link FileInfo} object
     * and adds it to a local index before merging it into the global index.
     *
     * @param file     The file to be indexed.
     * @param fileInfo The metadata information of the file.
     */
    @Override
    public void indexFile(File file, FileInfo fileInfo) {
        try {
            // Local index to store file names and their corresponding FileInfo objects
            Map<String, List<FileInfo>> localIndex = new TreeMap<>();

            // If the file name is not in the local index, add it with an empty list
            localIndex.putIfAbsent(fileInfo.getName(), new ArrayList<>());

            // Retrieve the list of files associated with this name and add the new file
            List<FileInfo> fileInfoList = localIndex.get(fileInfo.getName());
            fileInfoList.add(fileInfo);

            // Update the local index with the modified list
            localIndex.put(fileInfo.getName(), fileInfoList);

            // Merge the local index with the global index
            mergeWithGlobalIndex(localIndex);
        } catch (Exception e) {
            logger.warn("Error processing file {}: {}", file.getName(), e.getMessage());
        }
    }

    /**
     * Merges the local name index with the global index stored in {@link IndexStore}.
     *
     * @param localIndex The local name-based index to be merged into the global index.
     */
    private void mergeWithGlobalIndex(Map<String, List<FileInfo>> localIndex) {
        IndexStore.getInstance().updateNameIndex(localIndex);
    }
}
