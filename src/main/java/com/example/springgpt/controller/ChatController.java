package com.example.springgpt.controller;

import com.example.springgpt.history.MessageHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final OllamaChatModel ollamaChatModel;
    private final VectorStore vectorStore;
    private final MessageHistoryService messageHistoryService;

    @PostMapping
    public String chat(@RequestParam String id, @RequestBody String message) {
        QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore);
        log.info("User: {}", message);
        String chatBotMessage = ChatClient.builder(ollamaChatModel)
                .build().prompt()
                .advisors(questionAnswerAdvisor)
                .user(message)
                .messages(messageHistoryService.findAllBySessionId(id))
                .call()
                .content();
        log.info("Chat bot: {}", chatBotMessage);
        messageHistoryService.patchHistory(id, new UserMessage(message), new AssistantMessage(chatBotMessage));
        return chatBotMessage;
    }
}