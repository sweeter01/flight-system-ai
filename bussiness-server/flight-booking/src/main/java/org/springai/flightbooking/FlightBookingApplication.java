package org.springai.flightbooking;

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
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@ComponentScan(basePackages = {"org.springai.flightbooking", "com.power"})
@EnableMethodSecurity
@MapperScan("org.springai.flightbooking.mapper")
@EnableFeignClients
public class FlightBookingApplication {

    private static final Logger logger = LoggerFactory.getLogger(FlightBookingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FlightBookingApplication.class, args);
    }

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(EmbeddingModel embeddingModel, VectorStore vectorStore,
                                                       @Value("classpath:rag/terms-of-service.txt") Resource termsOfServiceDocs) {

        return args -> {
            vectorStore.write(new TokenTextSplitter()
                    .transform(new TextReader(termsOfServiceDocs).read()));
            vectorStore.similaritySearch("退订航班").forEach(doc -> {
                logger.info("Similar Document: {}", doc.getText());
            });
        };
    }

}