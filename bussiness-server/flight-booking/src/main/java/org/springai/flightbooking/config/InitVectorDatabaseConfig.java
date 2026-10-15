//package org.springai.flightbooking.config;
//
//
//import ch.qos.logback.core.util.MD5Util;
//import jakarta.annotation.PostConstruct;
//import org.apache.commons.codec.digest.Md5Crypt;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.reader.TextReader;
//import org.springframework.ai.transformer.splitter.TokenTextSplitter;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.nio.charset.Charset;
//import java.util.List;
//// 初始化向量数据库配置类，手动去重，避免重复添加
//@Configuration
//public class InitVectorDatabaseConfig {
//
//    @Autowired
//    private VectorStore vectorStore;
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    @Value("classpath:rag/terms-of-service.txt")
//    private Resource file;
//
//    @PostConstruct // 初始化方法，项目启动时执行
//    public void init() {
//        TextReader textReader = new TextReader(file);
//        textReader.setCharset(Charset.defaultCharset());
//
//        List<Document> transform = new TokenTextSplitter().transform(textReader.read());
////        vectorStore.add(transform);
//
//        // 直接用 file 获取文件名
//        String txt = file.getFilename();
//        String md5 = Md5Crypt.md5Crypt(txt.getBytes());
//        String secret = "vector-Exist" + md5;
//        Boolean b = redisTemplate.opsForValue().setIfAbsent(secret, "1");
//
//        System.out.println(b); //输出是否已经在redis中存在ops.txt向量
//
//        if (Boolean.TRUE.equals(b))
//        {
//            vectorStore.add(transform);//不存在则添加
//        }else {
//            throw new RuntimeException("向量已存在");
//        }
//
//    }
//
//}
