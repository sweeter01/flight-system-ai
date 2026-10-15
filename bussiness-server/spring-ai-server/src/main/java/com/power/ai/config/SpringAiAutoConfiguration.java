package com.power.ai.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import redis.clients.jedis.JedisPooled;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
public class SpringAiAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpringAiAutoConfiguration.class);



    /**
     * 配置Redis模板
     * @param redisConnectionFactory Redis连接工厂
     * @return Redis模板
     * */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public TtlRedisChatMemoryRepository ttlRedisChatMemoryRepository(StringRedisTemplate redisTemplate) {
        return new TtlRedisChatMemoryRepository(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(java.time.Duration.ofSeconds(120));
        return RestClient.builder().requestFactory(requestFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisVectorStore vectorStore(EmbeddingModel embeddingModel,
                                        @Value("${spring.data.redis.host:192.168.100.128}") String redisHost,
                                        @Value("${spring.data.redis.port:6388}") int redisPort) {
        return RedisVectorStore.builder(new JedisPooled(redisHost, redisPort), embeddingModel)
                .indexName("power-index")
                .prefix("power-prefix")
                .initializeSchema(true)
                .build();
    }



    // TTL Redis ChatMemoryRepository 实现
    public static class TtlRedisChatMemoryRepository implements ChatMemoryRepository {

        private static final Logger logger = LoggerFactory.getLogger(TtlRedisChatMemoryRepository.class);
        private static final String KEY_PREFIX = "chat:memory:";
        private static final int TTL_DAYS = 2;
        private static final ObjectMapper MAPPER = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        private final StringRedisTemplate redisTemplate;

        public TtlRedisChatMemoryRepository(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /*
        * 获取所有会话ID
        * */
        @Override
        public List<String> findConversationIds() {
            Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                return List.of();
            }
            return keys.stream()
                    .map(key -> key.substring(KEY_PREFIX.length()))
                    .toList();
        }

        /*
        * 根据会话ID获取所有消息
        * */
        @Override
        public List<Message> findByConversationId(String conversationId) {
            String key = KEY_PREFIX + conversationId;
            List<String> rawMessages = redisTemplate.opsForList().range(key, 0, -1);
            if (rawMessages == null || rawMessages.isEmpty()) {
                return List.of();
            }
            List<Message> messages = new ArrayList<>();
            for (String raw : rawMessages) {
                try {
                    MessageRecord record = MAPPER.readValue(raw, MessageRecord.class);
                    if (MessageType.USER.getValue().equals(record.role())) {
                        messages.add(new UserMessage(record.content()));
                    } else if (MessageType.ASSISTANT.getValue().equals(record.role())) {
                        messages.add(new AssistantMessage(record.content()));
                    }
                } catch (Exception e) {
                    logger.warn("Failed to deserialize message: {}", raw, e);
                }
            }
            return messages;
        }

        @Override
        public void saveAll(String conversationId, List<Message> messages) {
            String key = KEY_PREFIX + conversationId;
            redisTemplate.delete(key);
            List<String> records = messages.stream()
                    .map(msg -> {
                        try {
                            return MAPPER.writeValueAsString(
                                    new MessageRecord(msg.getMessageType().getValue(), msg.getText()));
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to serialize message", e);
                        }
                    })
                    .toList();
            redisTemplate.opsForList().rightPushAll(key, records);
            redisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
        }

        /**
         * 删除指定会话ID的所有消息
         * */
               @Override
        public void deleteByConversationId(String conversationId) {
            redisTemplate.delete(KEY_PREFIX + conversationId);
        }

        record MessageRecord(String role, String content) {
        }
    }
}
