package com.example.File_Retrieval_Engine.service.impl;

import com.example.File_Retrieval_Engine.exception.EmptyDataSetException;
import com.example.File_Retrieval_Engine.model.FileInfo;
import com.example.File_Retrieval_Engine.model.SearchingCriteria;
import com.example.File_Retrieval_Engine.service.Engine;
import com.example.File_Retrieval_Engine.service.IndexStore;
import com.example.File_Retrieval_Engine.strategy.IndexingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ProcessingEngine is responsible for indexing files and folders
 * and performing searches based on various criteria.
 */
@Service
public class ProcessingEngine implements Engine {

    private final List<IndexingStrategy> strategies;
    private static final Logger logger = LoggerFactory.getLogger(ProcessingEngine.class);
    private final ExecutorService executorService;

    /**
     * Constructs a ProcessingEngine with a list of indexing strategies.
     *
     * @param strategies List of indexing strategies to apply when indexing files.
     */
    public ProcessingEngine(List<IndexingStrategy> strategies) {
        this.strategies = strategies;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Indexes files from the specified path.
     *
     * @param path The directory path to index.
     * @throws EmptyDataSetException if the provided dataset is empty.
     */
    @Override
    public void index(String path) throws EmptyDataSetException {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("Start indexing...");

            File[] files = getFolders(path);

            for (File file : files) {
                if (file.isDirectory()) {
                    indexFolder(file);
                } else {
                    indexFile(file);
                }
            }

            executorService.shutdown();
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                logger.warn("Executor did not terminate in the given time.");
                executorService.shutdownNow();
            }

            long endTime = System.currentTimeMillis();
            logger.info("Finished indexing! Time taken: {} seconds", (endTime - startTime) / 1000.0);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Retrieves the list of files and folders in a given directory.
     *
     * @param path The directory path.
     * @return An array of files and folders.
     * @throws EmptyDataSetException if the directory is empty.
     */
    private File[] getFolders(String path) throws EmptyDataSetException {
        File[] files = new File(path).listFiles();
        if (files == null || files.length == 0) {
            logger.warn("No files found in the specified path: {}", path);
            throw new EmptyDataSetException("Provided dataset is empty!");
        }
        return files;
    }

    /**
     * Recursively indexes the files in a given folder.
     *
     * @param folder The folder to index.
     */
    private void indexFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                indexFile(file);
            }
        }
    }

    /**
     * Indexes a single file.
     *
     * @param file The file to index.
     */
    private void indexFile(File file) {
        FileInfo fileInfo = new FileInfo(
                file.getName(),
                file.getPath(),
                file.length(),
                file.lastModified());

        for (IndexingStrategy strategy : strategies) {
            executorService.submit(() -> strategy.indexFile(file, fileInfo));
        }
    }

    /**
     * Searches for files based on the provided criteria.
     * The search combines results from multiple filtering conditions:
     * - File name search
     * - File size range search
     * - File content search
     * <p>
     * The method works as follows:
     * 1. It initializes a list (`resultSet`) to store sets of matching files from different criteria.
     * 2. It applies each search filter (file name, size, and content) and adds the matching file sets to `resultSet`.
     * 3. It finds the common files among all search results using set intersection.
     * 4. Finally, it returns a list of files that match all the specified criteria.
     *
     * @param criteria The criteria containing file name, size range, and content search terms.
     * @return A list of files that match all provided search conditions.
     */
    @Override
    public List<FileInfo> search(SearchingCriteria criteria) {
        List<Set<FileInfo>> resultSet = new ArrayList<>();

        if (criteria.getFileName() != null && !criteria.getFileName().isEmpty()) {
            resultSet.add(nameCriteria(criteria.getFileName()));
        }
        if (criteria.getMinSize() != null || criteria.getMaxSize() != null) {
            resultSet.add(sizeCriteria(criteria.getMinSize(), criteria.getMaxSize()));
        }
        if (criteria.getContentSearchTerm() != null && !criteria.getContentSearchTerm().isEmpty()) {
            resultSet.add(contentCriteria(criteria.getContentSearchTerm()));
        }

        Set<FileInfo> commonFiles = new HashSet<>(resultSet.getFirst());
        for (Set<FileInfo> fileInfos : resultSet) {
            commonFiles.retainAll(fileInfos);
        }
        return new ArrayList<>(commonFiles);
    }

    /**
     * Searches for files by name.
     *
     * @param fileName The file name to search for.
     * @return A set of matching FileInfo objects.
     */
    private Set<FileInfo> nameCriteria(String fileName) {
        List<FileInfo> fileInfos = IndexStore.getInstance().getGlobalNameIndex().get(fileName);
        return fileInfos != null ? new HashSet<>(fileInfos) : new HashSet<>();
    }

    /**
     * Searches for files within a given size range.
     *
     * @param minSize The minimum file size.
     * @param maxSize The maximum file size.
     * @return A set of matching FileInfo objects.
     */
    private Set<FileInfo> sizeCriteria(Long minSize, Long maxSize) {
        Set<FileInfo> fileInfos = new HashSet<>();
        for (Map.Entry<Long, List<FileInfo>> entry : IndexStore.getInstance().getGlobalSizeIndex().entrySet()) {
            Long fileSize = entry.getKey();
            boolean matchesMin = minSize == null || fileSize >= minSize;
            boolean matchesMax = maxSize == null || fileSize <= maxSize;
            if (matchesMin && matchesMax) {
                fileInfos.addAll(entry.getValue());
            }
        }
        return fileInfos;
    }

    /**
     * Searches for files containing specific content terms.
     *
     * @param words The words to search for.
     * @return A set of matching FileInfo objects.
     */
    private Set<FileInfo> contentCriteria(String words) {
        List<Set<FileInfo>> fileInfos = new ArrayList<>();
        String[] params = words.split(" ");
        for (String param : params) {
            Map<FileInfo, Integer> entry = IndexStore.getInstance().getGlobalContentIndex().get(param.toLowerCase());
            if (entry != null) {
                fileInfos.add(entry.keySet());
            }
        }
        Set<FileInfo> commonFiles = new HashSet<>(fileInfos.getFirst());
        for (Set<FileInfo> file : fileInfos) {
            commonFiles.retainAll(file);
        }
        return commonFiles;
    }
}
