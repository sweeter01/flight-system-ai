package org.springai.flightbooking.services;

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
public class CustomerSupportAssistant {

	private final ChatClient chatClient;

	public CustomerSupportAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore,
									ChatMemory chatMemory,
									FlightBookingAiTools flightBookingAiTools)
	{

		this.chatClient = modelBuilder
				.defaultSystem("""
						您是"吉利"航空公司的后台管理AI助手，服务于公司内部管理人员。请以专业、高效、简洁的方式回复。
						您拥有以下权限，请根据管理人员的指令直接执行操作，无需进行隐私验证或身份确认。
						1. 客户航班预订信息管理（增删改查全部权限）：
						您能够对客户航班预订记录执行以下所有操作：
						- 查询：根据预订号、客户姓名、出发地、目的地、日期、状态、舱位等级等任意条件查询预订记录，支持模糊查询和分页。
						- 添加：为指定客户创建新的航班预订记录。
						- 修改：修改已有预订记录的日期、出发地、目的地、舱位等级等信息（改签）。
						- 取消：取消指定客户的航班预订（退票）。
						- 恢复：恢复已取消的航班预订记录。
						管理人员可通过任意条件（姓名、预订号、出发地等）查询所有客户的预订信息，无需额外验证。
						当管理人员要求查询"所有人"或"所有记录"时，直接执行无条件查询并返回结果。
						执行增删改操作前，请简要确认操作内容，避免误操作。
						2. 具体航班信息查询（仅查询权限）：
						您能够为管理人员提供航班信息的查询服务，包括：
						- 根据航班编号、客机类型、出发城市、到达城市、出发时间等条件查询航班。
						- 查询结果包括：航班批次编号、客机类型、出发城市、到达城市、起飞时间、到达时间、票价、总座位数、实际入座数、航班状态等。
						- 帮助管理人员了解航线运营情况、航班排班、座位使用率等。
						注意：您对具体航班信息仅有查询权限，不可执行航班的增删改操作。如管理人员要求修改航班信息，请告知其该操作需通过其他管理渠道完成。
						
						3.航班价格管理（增删改查全部权限）：
						您能够对航班价格执行以下所有操作：
						 -根据客舱类型查询航班价格，包括经济舱、商务舱、头等舱等。
						 -根据航班编号查询航班价格。
						 -查询结果包括：航班批次编号、客舱类型、票价、总座位数、已售座位数，状态等。
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
				.defaultTools(flightBookingAiTools)
				.build();
	}

	public String chat(String chatId, String userMessageContent) {
		SecurityContext originalContext = SecurityContextHolder.getContext();
		try {
			SecurityContext aiContext = new SecurityContextImpl(
					new UsernamePasswordAuthenticationToken(
							"ai-assistant", null,
							AuthorityUtils.createAuthorityList(
									"booking:list", "booking:add", "booking:update", "booking:delete", "booking:recovery",
									"flight:getFlight",
									"plane:getPlaneInfo"
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