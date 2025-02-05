package com.example.File_Retrieval_Engine.service;

import com.example.File_Retrieval_Engine.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton class to manage a shared global index over the HTTP server thread.
 * It provides thread-safe updates to the index.
 */
@Component
public class IndexStore {

    public static IndexStore indexStore;
    private final Map<String, Map<FileInfo, Integer>> globalContentIndex = new TreeMap<>();
    private final Map<Long, List<FileInfo>> globalSizeIndex = new TreeMap<>();
    private final Map<String, List<FileInfo>> globalNameIndex = new TreeMap<>();
    private static final Logger logger = LoggerFactory.getLogger(IndexStore.class);
    private final Lock contentIndexLock = new ReentrantLock();
    private final Lock sizeIndexLock = new ReentrantLock();
    private final Lock nameIndexLock = new ReentrantLock();

    private IndexStore(){}

    public static IndexStore getInstance(){
        synchronized (IndexStore.class){
            if(indexStore == null){
                indexStore = new IndexStore();
            }
            return indexStore;
        }
    }

    /**
     * Updates the global ContentIndex with a local index from a worker thread.
     *
     * @param localIndex the local index to merge
     */
    public void updateContentIndex(Map<String, Map<FileInfo, Integer>> localIndex) {
        contentIndexLock.lock(); // Lock for updating the global index
        try {
            localIndex.forEach((term, localDocMap) -> {
                globalContentIndex.merge(term, localDocMap, (globalDocMap, newLocalDocMap) -> {
                    newLocalDocMap.forEach((doc, frequency) ->
                            globalDocMap.merge(doc, frequency, Integer::sum));
                    return globalDocMap;
                });
            });
        } finally {
            contentIndexLock.unlock(); // Ensure the lock is released
        }
    }

    /**
     * Updates the global SizeIndex with a local index from a worker thread.
     *
     * @param localIndex the local index to merge
     */
    public void updateSizeIndex(Map<Long, List<FileInfo>> localIndex) {
        sizeIndexLock.lock(); // Lock for updating the global index
        try {
            localIndex.forEach((term, localList) -> {
                globalSizeIndex.merge(term, localList, (existingList, newList) -> {
                    existingList.addAll(newList);
                    return existingList;
                });
            });
        } finally {
            sizeIndexLock.unlock(); // Ensure the lock is released
        }
    }

    /**
     * Updates the global ContentIndex with a local index from a worker thread.
     *
     * @param localIndex the local index to merge
     */
    public void updateNameIndex(Map<String, List<FileInfo>> localIndex) {
        nameIndexLock.lock(); // Lock for updating the global index
        try {
            localIndex.forEach((term, localList) -> {
                globalNameIndex.merge(term, localList, (existingList, newList) -> {
                    existingList.addAll(newList);
                    return existingList;
                });
            });
        } finally {
            nameIndexLock.unlock(); // Ensure the lock is released
        }
    }

    public Map<String, Map<FileInfo, Integer>> getGlobalContentIndex() {
        return globalContentIndex;
    }

    public Map<Long, List<FileInfo>> getGlobalSizeIndex() {
        return globalSizeIndex;
    }

    public Map<String, List<FileInfo>> getGlobalNameIndex() {
        return globalNameIndex;
    }

}
