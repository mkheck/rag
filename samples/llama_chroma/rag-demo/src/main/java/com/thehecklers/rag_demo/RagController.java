package com.thehecklers.rag_demo;

import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.Media;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;

@RestController
public class RagController {
    private final ChatClient client;
    private final ChatModel chatModel;
    private final VectorStore db;

    public RagController(ChatClient.Builder builder, ChatModel chatModel, VectorStore db) {
        this.client = builder.build();
        this.chatModel = chatModel;
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

        db.add(splitter.apply(tikaDocumentReader.get()));

        logger.info("Vector store population complete!");

        return "Populated vector store with " + filepath;
    }

    @GetMapping("/rag")
    public String getRagResponse(@RequestParam(defaultValue = "Airspeeds") String message) {
        return client.prompt()
                .user(message)
                .advisors(new QuestionAnswerAdvisor(db))
                .call()
                .content();
    }

    @GetMapping("/mm")
    public String getMultimodalResponse(@RequestParam(defaultValue = "/Users/markheckler/files/testimage.jpg") String imagePath,
                                        @RequestParam(defaultValue = "What's in this image?") String message) throws MalformedURLException {
        var imageType = imagePath.endsWith(".png") ? MimeTypeUtils.IMAGE_PNG : MimeTypeUtils.IMAGE_JPEG;
        var media = imagePath.startsWith("http")
                ? new Media(imageType, new URL(imagePath))
                : new Media(imageType, new FileSystemResource(imagePath));

        var userMessage = new UserMessage(message, media);
        var systemMessage = new SystemMessage("If you can't definitively identify the image, make your best guess.");

        return chatModel.call(userMessage, systemMessage);
    }

    @GetMapping("/imagerag")
    public String getImageRagResponse(@RequestParam(defaultValue = "/Users/markheckler/files/testimage.jpg") String imagePath,
                                      @RequestParam(defaultValue = "Tell me everything you can about this image") String message) throws MalformedURLException {
        // First analyze the image using AI, then use RAG to search our docs for information specific to our domain
        return getRagResponse(getMultimodalResponse(imagePath, message));
    }
}
