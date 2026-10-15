package com.power.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrderSupportAssistant {

    private final ChatClient chatClient;

    public OrderSupportAssistant(ChatClient.Builder modelBuilder,
                                 VectorStore vectorStore,
                                 ChatMemory chatMemory,
                                 OrderAiTools orderAiTools) {

        this.chatClient = modelBuilder
                .defaultSystem("""
                        您是"吉利"航空公司的订单管理AI助手，服务于公司内部管理人员。请以专业、高效、简洁的方式回复。
                        您拥有以下权限，请根据管理人员的指令直接执行操作，无需进行隐私验证或身份确认。
                        
                        1. 乘客信息管理：
                        您能够对乘客记录执行以下操作：
                        - 条件查询：根据姓名、年龄范围、性别、身份证号、用户名等条件查询乘客列表，支持分页。
                        - 查询订单详情：根据乘客ID查询该乘客的所有历史订单详情，包含航班预订信息和优惠券信息。
                        
                        2. 订单信息管理：
                        您能够对订单记录执行以下操作：
                        - 查询：根据订单编码、用户姓名、订单状态、支付方式查询订单列表，支持分页。
                        - 取消订单：根据航班预订号取消订单，将订单状态设为已取消。
                        
                        3. 优惠券管理：
                        您能够对优惠券记录执行以下操作：
                        - 查询：根据优惠券编码、类型、名称、状态查询优惠券列表。
                        
                        管理人员可通过任意条件查询所有信息，无需额外验证。
                        当管理人员要求查询"所有订单"或"所有乘客"时，直接执行无条件查询并返回结果。
                        执行修改或取消操作前，请简要确认操作内容，避免误操作。
                        
                        4. 回复规范：
                        - 使用中文回复。
                        - 查询结果以清晰的表格或列表形式呈现。
                        - 操作完成后明确反馈操作结果。
                        - 如查询无结果，如实告知并建议调整查询条件。
                        
                        今天的日期是 {current_date}.
                        """)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        RetrievalAugmentationAdvisor.builder()
                                .documentRetriever(VectorStoreDocumentRetriever.builder()
                                        .vectorStore(vectorStore)
                                        .topK(4)
                                        .similarityThreshold(0.0)
                                        .build())
                                .build()
                )
                .defaultTools(orderAiTools)
                .build();
    }

    public String chat(String chatId, String userMessageContent) {
        SecurityContext originalContext = SecurityContextHolder.getContext();
        try {
            SecurityContext aiContext = new SecurityContextImpl(
                    new UsernamePasswordAuthenticationToken(
                            "ai-assistant", null,
                            AuthorityUtils.createAuthorityList(
                                    "passenger:list", "passenger:update", "passenger:add", "passenger:delete",
                                    "order:list", "order:update", "order:delete", "order:cancel",
                                    "coupon:list", "coupon:update", "coupon:add", "coupon:delete"
                            )
                    )
            );
            SecurityContextHolder.setContext(aiContext);

            return chatClient.prompt()
                    .system(s -> s.param("current_date", LocalDate.now().toString()))
                    .user(userMessageContent)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                    .call()
                    .content();
        } finally {
            SecurityContextHolder.setContext(originalContext);
        }
    }
}