package com.power;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@MapperScan("com.power.mapper")
@ComponentScan(basePackages = {"com.power"})
public class ManagerServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(ManagerServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ManagerServiceApplication.class,args);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(EmbeddingModel embeddingModel, VectorStore vectorStore,
                                                       @Value("classpath:rag/terms-of-service.txt") Resource termsOfServiceDocs) {
        return args -> {
            vectorStore.write(new TokenTextSplitter()
                    .transform(new TextReader(termsOfServiceDocs).read()));
            vectorStore.similaritySearch("管理员操作").forEach(doc -> {
                logger.info("Similar Document: {}", doc.getText());
            });
        };
    }
}