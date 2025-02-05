package com.example.File_Retrieval_Engine.controller;

import com.example.File_Retrieval_Engine.model.SearchingCriteria;
import com.example.File_Retrieval_Engine.service.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final Engine processingEngine;
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    public SearchController(Engine processingEngine) {
        this.processingEngine = processingEngine;
    }

    @PostMapping
    public ResponseEntity<?> search(@RequestBody SearchingCriteria criteria){
        try {
            return ResponseEntity.ok(processingEngine.search(criteria));
        } catch (Exception e) {
            logger.warn("Error processing a search operation with criteria : {} ",e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
