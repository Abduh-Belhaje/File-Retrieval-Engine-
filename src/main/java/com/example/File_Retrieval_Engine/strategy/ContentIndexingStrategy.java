package com.example.File_Retrieval_Engine.strategy;

import com.example.File_Retrieval_Engine.model.FileInfo;
import com.example.File_Retrieval_Engine.service.IndexStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Strategy for indexing file content by extracting words and storing their frequencies.
 */
@Component
public class ContentIndexingStrategy implements IndexingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ContentIndexingStrategy.class);

    /**
     * Indexes a given file by extracting words and updating their frequencies in the global index.
     *
     * @param file     The file to be indexed.
     * @param fileInfo Metadata information about the file.
     */
    @Override
    public void indexFile(File file, FileInfo fileInfo) {
        Map<String, Map<FileInfo, Integer>> localIndex = new TreeMap<>();  // Local index for this file
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            // Read file line by line
            while ((line = br.readLine()) != null) {
                String[] words = extractTerms(line);  // Extract words from each line

                for (String word : words) {
                    String term = word.toLowerCase();  // Normalize to lowercase

                    // If the term is not in the local index, add it with an empty document-frequency map
                    localIndex.putIfAbsent(term, new HashMap<>());

                    // Get the document-frequency map for this term
                    Map<FileInfo, Integer> docMap = localIndex.get(term);

                    // Update the frequency of the term in this document (file.getName())
                    docMap.put(fileInfo, docMap.getOrDefault(fileInfo, 0) + 1);
                }
            }

            // Update global index with the local index
            mergeWithGlobalIndex(localIndex);
        } catch (Exception e) {
            logger.warn("Error processing file {}: {}", file.getName(), e.getMessage());
        }
    }

    /**
     * Extracts words/terms from a line of text, splitting by non-alphanumeric characters.
     *
     * @param line The input line of text.
     * @return An array of extracted words.
     */
    private String[] extractTerms(String line) {
        return line.toLowerCase().split("\\W+");
    }

    /**
     * Merges the local index with the global index stored in IndexStore.
     *
     * @param localIndex The local index to merge.
     */
    private void mergeWithGlobalIndex(Map<String, Map<FileInfo, Integer>> localIndex) {
        IndexStore.getInstance().updateContentIndex(localIndex);
    }
}
