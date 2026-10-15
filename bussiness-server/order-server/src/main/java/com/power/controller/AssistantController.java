package com.power.controller;

import com.power.service.OrderSupportAssistant;
import com.power.model.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order/assistant")
@RestController
@CrossOrigin
public class AssistantController {

    private final OrderSupportAssistant agent;

    public AssistantController(OrderSupportAssistant agent) {
        this.agent = agent;
    }

    @RequestMapping(path = "/chat")
    public Result<String> chat(String chatId, String message) {

        if (message == null || message.trim().isEmpty()) {
            return Result.success("请输入有效消息");
        }

        return Result.success(agent.chat(chatId, message));
    }
}