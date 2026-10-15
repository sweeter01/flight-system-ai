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
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@SpringBootApplication
@MapperScan("com.power.mapper")
@EnableMethodSecurity
@EnableDiscoveryClient
@EnableFeignClients
public class OrderApplication {

    private static final Logger logger = LoggerFactory.getLogger(OrderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }


    /**
     * 向量数据库中写入订单管理相关文档
     * @param embeddingModel 嵌入模型
     * @param vectorStore 向量数据库
     * @param resource 订单管理相关文档资源
     * @return 命令行运行器
     * */
    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(EmbeddingModel embeddingModel, VectorStore vectorStore,
                                                       @Value("classpath:rag/terms-of-service.txt") Resource resource) {

        return args -> {
            var existingDocs = vectorStore.similaritySearch("订单管理");
            if (!existingDocs.isEmpty()) {
                logger.info("向量数据库中已存在RAG数据，跳过写入。");
                return;
            }
            vectorStore.write(new TokenTextSplitter()
                    .transform(new TextReader(resource).read()));
            vectorStore.similaritySearch("订单管理").forEach(doc -> {
                logger.info("Similar Document: {}", doc.getText());
            });
        };
    }
}