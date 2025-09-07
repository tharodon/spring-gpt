package com.example.springgpt.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHistoryService implements ChatMemory {

    private final MessageHistoryRepository messageHistoryRepository;

    @Override
    public void add(String conversationId, Message message) {
        MessageHistory messageHistory = MessageHistory.builder()
                .sessionId(conversationId)
                .message(message.getText())
                .type(message.getMessageType())
                .build();
        messageHistoryRepository.save(messageHistory);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        messages.forEach(message -> add(conversationId, message));
    }

    @Override
    public List<Message> get(String conversationId) {
        return messageHistoryRepository.findAllBySessionIdOrderByCreatedAt(conversationId).stream()
                .<Message>map(it -> switch (it.getType()) {
                    case USER -> new UserMessage(it.getMessage());
                    case ASSISTANT -> new AssistantMessage(it.getMessage());
                    case null, default -> throw new RuntimeException();
                })
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        log.info("Clear message by session: {}", conversationId);
        messageHistoryRepository.deleteBySessionId(conversationId);
    }
}
