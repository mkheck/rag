package com.thehecklers.rag_demo;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AccessToken;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.util.ClientOptions;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.azure.AzureVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RagConfig {
    /*
    * This is a mashup of several approaches I've worked through per the Spring AI embeddings + vector store
    * sections for Azure AI Search. Commenting out the @Configuration, nothing happens here of course. But
    * the autoconfig does provide the same outcome as manual config in various fashions, so I've commented it
    * ALL out to establish a baseline.
    * MH
    * */

//    @Value("${spring.ai.vectorstore.azure.initialize-schema:false}")
//    private boolean initializeSchema;
//
//    private final String endPoint = System.getenv("AZURE_AI_SEARCH_ENDPOINT");
//    private final String apiKey = System.getenv("AZURE_AI_SEARCH_API_KEY");
//    private final String embDepName = System.getenv("SPRING_AI_AZURE_OPENAI_EMBEDDING_OPTIONS_DEPLOYMENT_NAME");

//    @Bean
//    public TokenCredential tokenCredential() {
//        return new DefaultAzureCredentialBuilder().build();
//    }

//    @Bean
//    public SearchIndexClient searchIndexClient() {
//        return new SearchIndexClientBuilder().endpoint(endPoint)
//                .credential(new AzureKeyCredential(apiKey))
//                .buildClient();
//    }

//    @Bean
////    public EmbeddingModel embeddingModel(OpenAIClient client) {
//    public EmbeddingModel embeddingModel() {
//        var openAIClient = new OpenAIClientBuilder()
//                .credential(new AzureKeyCredential(System.getenv("AZURE_OPENAI_API_KEY")))
//                .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
//                .clientOptions(new ClientOptions())
//                .buildClient();
//
////        return new AzureOpenAiEmbeddingModel(client,
//        return new AzureOpenAiEmbeddingModel(openAIClient,
//                MetadataMode.EMBED,
//                AzureOpenAiEmbeddingOptions.builder().withDeploymentName(embDepName).build());
//    }

//    @Bean
//    public VectorStore vectorStore(SearchIndexClient searchIndexClient, EmbeddingModel embeddingModel) {
//        return new AzureVectorStore(searchIndexClient, embeddingModel, initializeSchema);
//    }
}
