package com.example.springgpt.controller;

import com.example.springgpt.tool.DateTimeTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @PostMapping
    public String chat(@RequestParam String id, @RequestBody String message) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(message)
                .topK(1)
                .similarityThreshold(0.5)
                .build());
        log.info("User: {}", message);
        String chatBotMessage = chatClient.prompt(message)
                .tools(new DateTimeTools())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
                .stream()
                .content()
                .doOnNext(System.out::print)
                .collectList()
                .block()
                .stream()
                .collect(Collectors.joining());
        log.info("Chat bot: {}", chatBotMessage);
        return chatBotMessage;
    }
}