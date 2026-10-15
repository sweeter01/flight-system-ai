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
public class AddrSupportAssistant {

    private final ChatClient chatClient;

    public AddrSupportAssistant(ChatClient.Builder modelBuilder,
                                VectorStore vectorStore,
                                ChatMemory chatMemory,
                                AddrAiTools addrAiTools) {

        this.chatClient = modelBuilder
                .defaultSystem("""
                        您是"吉利"航空公司的运营地址管理AI助手，服务于公司内部管理人员。请以专业、高效、简洁的方式回复。
                        您拥有以下权限，请根据管理人员的指令直接执行操作，无需进行隐私验证或身份确认。
                        1. 运营城市信息管理（增删改查全部权限）：
                        您能够对运营城市记录执行以下所有操作：
                        - 查询：根据城市编码、城市名称等条件查询城市列表，支持模糊查询和分页。
                        - 添加：为系统新增运营城市记录，需提供城市编码、城市名称、机场名称。
                        - 修改：修改已有城市记录的编码、名称、机场名称、运营状态等信息。
                        - 删除：删除指定的城市运营记录。
                        管理人员可通过任意条件查询所有城市信息，无需额外验证。
                        当管理人员要求查询"所有城市"或"所有记录"时，直接执行无条件查询并返回结果。
                        执行增删改操作前，请简要确认操作内容，避免误操作。
                        2. 回复规范：
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
                .defaultTools(addrAiTools)
                .build();
    }

    public String chat(String chatId, String userMessageContent) {
        SecurityContext originalContext = SecurityContextHolder.getContext();
        try {
            SecurityContext aiContext = new SecurityContextImpl(
                    new UsernamePasswordAuthenticationToken(
                            "ai-assistant", null,
                            AuthorityUtils.createAuthorityList(
                                    "addr:listCity", "addr:addCity", "addr:updateCity", "addr:deleteCity"
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