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
public class ManagerSupportAssistant {

    private final ChatClient chatClient;

    public ManagerSupportAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore,
                                   ChatMemory chatMemory,
                                   ManagerAiTools managerAiTools) {

        this.chatClient = modelBuilder
                .defaultSystem("""
                        您是"吉利"航空公司的后台管理系统AI助手，服务于公司内部管理人员。请以专业、高效、简洁的方式回复。
                        您拥有以下权限，请根据管理人员的指令直接执行操作，无需进行隐私验证或身份确认。
                        1. 管理员（系统用户）信息管理（增删改查全部权限）：
                        您能够对系统管理员记录执行以下所有操作：
                        - 查询：根据用户ID、用户名等条件查询管理员记录，支持模糊查询和分页。
                        - 添加：创建新的系统管理员账号。
                        - 修改：修改已有管理员的用户名、昵称、状态等信息。
                        - 删除：删除指定的管理员账号，支持批量删除。
                        2. 角色信息管理（增删改查全部权限）：
                        您能够对系统角色记录执行以下所有操作：
                        - 查询：查询所有角色列表，或根据角色名称分页查询。
                        - 添加：创建新的系统角色。
                        - 修改：修改已有角色的名称、描述等信息。
                        - 删除：删除指定角色，支持批量删除。
                        3. 菜单权限信息管理（增删改查全部权限）：
                        您能够对系统菜单权限记录执行以下所有操作：
                        - 查询：查询所有菜单权限集合，或根据菜单ID查询详情。
                        - 添加：创建新的菜单或按钮权限。
                        - 修改：修改已有权限的名称、标识、排序、状态等信息。
                        - 删除：删除指定的菜单权限。
                        4. 系统操作日志查询（仅查询权限）：
                        您能够为管理人员提供操作日志的查询服务，包括：
                        - 根据用户ID、操作描述等条件分页查询日志。
                        - 查询结果包括：日志ID、用户ID、操作描述、请求方法、请求参数、IP地址、创建时间等。
                        注意：您对操作日志仅有查询权限，不可执行增删改操作。
                        5. 回复规范：
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
                .defaultTools(managerAiTools)
                .build();
    }

    public String chat(String chatId, String userMessageContent) {
        SecurityContext originalContext = SecurityContextHolder.getContext();
        try {
            SecurityContext aiContext = new SecurityContextImpl(
                    new UsernamePasswordAuthenticationToken(
                            "ai-assistant", null,
                            AuthorityUtils.createAuthorityList(
                                    "sys:user:page", "sys:user:info", "sys:user:update", "sys:user:delete",
                                    "sys:role:list", "sys:role:page", "sys:role:info", "sys:role:update", "sys:role:delete",
                                    "sys:menu:list", "sys:menu:info", "sys:menu:update", "sys:menu:delete",
                                    "sys:log:page"
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