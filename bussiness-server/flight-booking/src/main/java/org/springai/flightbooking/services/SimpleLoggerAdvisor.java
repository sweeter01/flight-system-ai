package org.springai.flightbooking.services;//package org.springai.flightbooking.services;
//
//import jakarta.validation.constraints.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.ai.chat.client.advisor.api.*;
//import org.springframework.ai.chat.messages.AssistantMessage;
//import org.springframework.ai.chat.model.Generation;
//import org.springframework.ai.chat.model.MessageAggregator;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.function.Function;
//
///**
// * @author xinlei
// */
//public class SimpleLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
//    private boolean protectFromBlocking = true;
//    private static final Logger logger = LoggerFactory.getLogger(SimpleLoggerAdvisor.class);
//    private AdvisedRequest before(AdvisedRequest request) {
//        return request;
//    }
//    @Override
//    public String getName() {
//        return this.getClass().getSimpleName();
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//    private void observeAfter(AdvisedResponse response) {
//        String model = response.response().getMetadata().getModel();  //only "", empty String
////        M6.1
////        Integer promptTokens = response.response().getMetadata().getUsage().getPromptTokens();
////        Integer completionTokens = response.response().getMetadata().getUsage().getCompletionTokens();
////        Integer totalTokens = response.response().getMetadata().getUsage().getTotalTokens();
////        log.info("{}, {}, {}", promptTokens, completionTokens, totalTokens);
//        Integer userPromptTokens = response.response().getMetadata().getUsage().getPromptTokens();
//        Long assistantTokens = response.response().getMetadata().getUsage().getGenerationTokens();
//        Integer totalTokens = response.response().getMetadata().getUsage().getTotalTokens();
//
//        // 先获取Generation对象
//        Generation generation = response.response().getResults().get(0);
//        // 从Generation对象中获取AssistantMessage对象
//        AssistantMessage assistantMessage = generation.getOutput();
//        // 从AssistantMessage对象中获取textContent的值
//        //String textContent = assistantMessage.textContent();
//        logger.info("{}, {}, {}, {}, {}", model, assistantMessage,userPromptTokens, assistantTokens, totalTokens);
//
//    }
//    @Override
//    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
//
//        // 获取用户发送的请求信息
//        String userRequest = advisedRequest.userText();
//        System.out.println("用户请求: " + userRequest);
//
//        // 调用链中的下一个Advisor，获取模型响应
//        AdvisedResponse response = chain.nextAroundCall(advisedRequest);
//        String aiResponse = response.adviseContext().toString();
//        System.out.println("AI响应: " + aiResponse);
//
//        // 这里可以添加将对话记录保存到文件、数据库等存储介质的逻辑
//        // 例如使用前面记录对话日志中的方式，调用工具类保存记录
//
//        return response;
//    }
//    @NotNull
//    @Override
//    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, @NotNull StreamAroundAdvisorChain chain) {
//        Flux<AdvisedResponse> advisedResponses = this.doNextWithProtectFromBlockingBefore(advisedRequest, chain, this::before);
//        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
//    }
//
//    protected Flux<AdvisedResponse> doNextWithProtectFromBlockingBefore(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain, Function<AdvisedRequest, AdvisedRequest> beforeAdvise) {
//        return this.protectFromBlocking ? Mono.just(advisedRequest).publishOn(Schedulers.boundedElastic()).map(beforeAdvise).flatMapMany((request) -> {
//            return chain.nextAroundStream(request);
//        }) : chain.nextAroundStream((AdvisedRequest)beforeAdvise.apply(advisedRequest));
//    }
//    /*@Override
//    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
//        //logger.info(advisedRequest.toString());
//
//        //logger.debug("BEFORE: {}", advisedRequest);
//
//        //Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
//
//        // 获取用户发送的请求信息
//        String userRequest = advisedRequest.userText();
//        System.out.println("stream用户请求: " + userRequest);
//
//        // 调用链中的下一个Advisor，获取模型响应
//        Flux<AdvisedResponse> response = chain.nextAroundStream(advisedRequest);
//
//        String aiResponse = response.toString();
//        System.out.println("streamAI响应: " + aiResponse);
//
//        Flux<AdvisedResponse> message = new MessageAggregator().aggregateAdvisedResponse(response,
//                advisedResponse -> logger.debug("AFTER: {}", advisedResponse));
//        //processResponse(message);
//        // 这里可以添加将对话记录保存到文件、数据库等存储介质的逻辑
//        // 例如使用前面记录对话日志中的方式，调用工具类保存记录
//        return message;
//    }*/
//
//    public void processResponse(Flux<AdvisedResponse> responseFlux) {
//        responseFlux.subscribe(
//                advisedResponse -> {
//                    // 假设直接从 advisedResponse 获取消息内容，需根据实际情况调整
//                    String message = extractMessage(advisedResponse);
//                    System.out.println("Received message: " + message);
//                },
//                error -> System.err.println("Error occurred: " + error),
//                () -> System.out.println("Stream completed")
//        );
//    }
//
//    private String extractMessage(AdvisedResponse advisedResponse) {
//        // 这里需要根据 AdvisedResponse 实际结构实现消息提取逻辑
//        // 例如，如果有 getContent 方法可以直接使用
//        return advisedResponse.adviseContext().toString();
//        //return null;
//    }
//}