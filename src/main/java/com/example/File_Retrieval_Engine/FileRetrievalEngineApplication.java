package com.example.File_Retrieval_Engine;

import com.example.File_Retrieval_Engine.service.Engine;
import com.example.File_Retrieval_Engine.service.impl.ProcessingEngine;
import com.example.File_Retrieval_Engine.strategy.ContentIndexingStrategy;
import com.example.File_Retrieval_Engine.strategy.IndexingStrategy;
import com.example.File_Retrieval_Engine.strategy.NameIndexingStrategy;
import com.example.File_Retrieval_Engine.strategy.SizeIndexingStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FileRetrievalEngineApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FileRetrievalEngineApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {

			List<IndexingStrategy> strategies = new ArrayList<>();
			strategies.add(new ContentIndexingStrategy());
			strategies.add(new SizeIndexingStrategy());
			strategies.add(new NameIndexingStrategy());
			Engine processingEngine = new ProcessingEngine(strategies);
			processingEngine.index("./DataSet");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
