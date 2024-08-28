package com.thehecklers.rag_demo;

import com.azure.ai.openai.OpenAIClient;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.List;

@RestController
public class RagController {
    private final ChatClient client;
    private final VectorStore db;

    public RagController(ChatClient.Builder builder, VectorStore db) {
        this.client = builder.build();
        this.db = db;
    }

    @GetMapping
    public String describe() {
        return """
                This is an application to populate and query a vector store, effectively turning loose 
                an AI on your data. This is a potentially powerful, focused tool, so as always, *verify your results*.
                
                To populate the vector store with embeddings for a supplied document, simply provide a 
                file path or URL that resolves to the document to be processed:
                
                /populate?filepath=<path or URL>
                
                To query the vector store for documents/data that matches your query, use the following endpoint:
                
                /rag?message=<your query>
                
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

        // MH: If you have sufficient rate limits/quota, uncomment this line and let 'er rip!
        db.add(splitter.apply(tikaDocumentReader.get()));
        // MH: OTOH, if you're hamstrung by rate limits, employ this less elegant workaround:
        // List<Document> docs = splitter.apply(tikaDocumentReader.get());
        // Write for loop like this but with 1 second delay between db.add() calls
        // docs.forEach(doc -> db.add(List.of(doc)));

        // Or use this Stream API call to add a 1 second delay between each db.add() call
        /*docs.forEach(System.out::println);
        docs.forEach(doc -> {
            logger.info("Saving doc: " + doc);
            db.add(List.of(doc));
            try {
                logger.info("Saved doc: " + doc);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });*/

        logger.info("Vector store population complete!");

        return "Populated vector store with " + filepath;
    }

    @GetMapping("/rag")
    public String getRagResponse(@RequestParam(defaultValue = "Airspeeds") String message) {
        return client.prompt()
                .user(message)
                .advisors(new QuestionAnswerAdvisor(db, SearchRequest.defaults()))
                .call()
                .content();
    }
}
