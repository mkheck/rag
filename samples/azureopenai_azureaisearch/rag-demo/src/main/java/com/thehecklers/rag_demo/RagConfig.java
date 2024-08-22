package com.thehecklers.rag_demo;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.azure.AzureVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    @Value("${spring.ai.vectorstore.azure.initialize-schema:false}")
    private boolean initializeSchema;

    private final String endPoint = System.getenv("AZURE_AI_SEARCH_ENDPOINT");
    private final String apiKey = System.getenv("AZURE_AI_SEARCH_API_KEY");

    @Bean
    public SearchIndexClient searchIndexClient() {
        return new SearchIndexClientBuilder().endpoint(endPoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildClient();
    }

    @Bean
    public VectorStore vectorStore(SearchIndexClient searchIndexClient, EmbeddingModel embeddingModel) {
        return new AzureVectorStore(searchIndexClient, embeddingModel, initializeSchema);
    }
}
