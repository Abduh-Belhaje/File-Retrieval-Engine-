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

@Service
public class ProcessingEngine implements Engine {

    private final List<IndexingStrategy> strategies;
    private static final Logger logger = LoggerFactory.getLogger(ProcessingEngine.class);
    private final ExecutorService executorService;

    public ProcessingEngine(List<IndexingStrategy> strategies) {
        this.strategies = strategies;
        this.executorService = Executors.newCachedThreadPool();
    }

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

            // Properly shutting down the executor
            executorService.shutdown(); // Stop accepting new tasks

            try {
                if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                    logger.warn("Executor did not terminate in the given time.");
                    executorService.shutdownNow(); // Force shutdown if still running
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt(); // Restore interrupted status
            }

            long endTime = System.currentTimeMillis();
            logger.info("Finished indexing!");
            logger.info("Indexing time: {} seconds", (endTime - startTime) / 1000.0);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred during indexing", e);
        }
    }


    private File[] getFolders(String path) throws EmptyDataSetException {
        File[] files = new File(path).listFiles();

        if (files == null || files.length == 0) {
            logger.warn("No files found in the specified path: {}", path);
            throw new EmptyDataSetException("provided DataSet is Empty !");
        }

        return files;
    }

    private void indexFolder(File folder) {
        // Recursively index the files in this folder
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                indexFile(file);
            }
        }
    }

    private void indexFile(File file){
        FileInfo fileInfo = new FileInfo(
                file.getName(),
                file.getPath(),
                file.length(),
                file.lastModified());

        for (IndexingStrategy strategy :strategies){
            executorService.submit(()-> { strategy.indexFile(file,fileInfo); });
        }

    }

    @Override
    public List<FileInfo> search(SearchingCriteria criteria) {
        List<Set<FileInfo>> resultSet = new ArrayList<>();

        if(criteria.getFileName() != null && !criteria.getFileName().isEmpty()) resultSet.add(nameCriteria(criteria.getFileName()));

        if (criteria.getMinSize() != null || criteria.getMaxSize() != null) resultSet.add(sizeCriteria(criteria.getMinSize(),criteria.getMaxSize()));

        if(criteria.getContentSearchTerm() != null && !criteria.getContentSearchTerm().isEmpty()) resultSet.add(contentCriteria(criteria.getContentSearchTerm()));

        Set<FileInfo> commonFiles = new HashSet<>(resultSet.getFirst());

        for(Set<FileInfo> fileInfos :resultSet){
            commonFiles.retainAll(fileInfos);
        }

        return new ArrayList<>(commonFiles);

    }

    private Set<FileInfo> nameCriteria(String fileName){
        if(fileName != null && !fileName.isEmpty()){
            List<FileInfo> fileInfos = IndexStore.gtInstance().getGlobalNameIndex().get(fileName);
            if (fileInfos != null) {
                return new HashSet<>(fileInfos);
            }
        }
        return new HashSet<>();
    }

    private Set<FileInfo> sizeCriteria(Long minSize , Long maxSize){
        Set<FileInfo> fileInfos = new HashSet<>();
        // Apply file size criteria
        if (minSize != null || maxSize != null) {
            for (Map.Entry<Long, List<FileInfo>> entry : IndexStore.getInstance().getGlobalSizeIndex().entrySet()) {
                Long fileSize = entry.getKey();

                // Check if the file size falls within the specified range
                boolean matchesMin = minSize == null || fileSize >= minSize;
                boolean matchesMax = maxSize == null || fileSize <= maxSize;

                if (matchesMin && matchesMax) {
                    fileInfos.addAll(entry.getValue());
                }
            }
        }
        return fileInfos;
    }

    private Set<FileInfo> contentCriteria(String words){
        List<Set<FileInfo>> fileInfos = new ArrayList<>();
        String[] params = words.split(" ");

        for (String param :params){
            Map<FileInfo,Integer> entry = IndexStore.getInstance().getGlobalContentIndex().get(param.toLowerCase());
            if(entry != null){
                fileInfos.add(entry.keySet());
            }
        }
        Set<FileInfo> commonFiles = new HashSet<>(fileInfos.getFirst());

        for(Set<FileInfo> file :fileInfos){
            commonFiles.retainAll(file);
        }
        return commonFiles;
    }
}
