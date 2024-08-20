package com.thehecklers.rag_demo;

import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
public class RagController {
    private final VectorStore db;

    public RagController(VectorStore db) {
        this.db = db;
    }

    @GetMapping
    public String describe() {
        return """
                This is an application to populate a vector store with embeddings for a supplied document.
                To use, simply provide a file path or URL that resolves to the document to be processed:
                
                /populate?filepath=<path or URL>
                
                DISCLAIMER: No warranty is provided or implied. Use at your own risk. :)
                """;
    }

    @GetMapping("/populate")
    public String populate(@RequestParam String filepath) throws MalformedURLException {
        /*
         Using GET and not POST since we're feeding in a file path or URL, then using that
         to navigate to the file and read it in. No request body needed or even desired.
         */
        var logger = LoggerFactory.getLogger(RagController.class);

        var importFile = filepath.startsWith("http")
                ? new UrlResource(filepath)
                : new FileSystemResource(filepath);
        var tikaDocumentReader = new TikaDocumentReader(importFile);
        var splitter = new TokenTextSplitter();

        logger.info("Populating vector store with " + filepath);

        db.add(splitter.apply(tikaDocumentReader.get()));

        logger.info("Vector store population complete!");

        return "Populated vector store with " + filepath;
    }
}
