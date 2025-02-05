package com.example.File_Retrieval_Engine.controller;

import com.example.File_Retrieval_Engine.model.SearchingCriteria;
import com.example.File_Retrieval_Engine.service.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller that handles search operations within the File Retrieval Engine.
 * It provides an API endpoint to search files based on given criteria.
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final Engine processingEngine;
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    /**
     * Constructor to initialize the search controller with the processing engine.
     *
     * @param processingEngine The engine responsible for executing search operations.
     */
    public SearchController(Engine processingEngine) {
        this.processingEngine = processingEngine;
    }

    /**
     * Handles search requests based on user-defined criteria.
     *
     * @param criteria The criteria used for searching files.
     * @return ResponseEntity containing the search results or an error message in case of failure.
     */
    @PostMapping
    public ResponseEntity<?> search(@RequestBody SearchingCriteria criteria){
        try {
            return ResponseEntity.ok(processingEngine.search(criteria));
        } catch (Exception e) {
            logger.warn("Error processing a search operation with criteria: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
