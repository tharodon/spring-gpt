package com.example.springgpt.history;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageHistoryService {

    private final MessageHistoryRepository messageHistoryRepository;

    public List<Message> findAllBySessionId(String sessionId) {
        return messageHistoryRepository.findAllBySessionIdOrderByCreatedAt(sessionId).stream()
                .<List<Message>>map(it -> List.of(
                        new UserMessage(it.getUserMessage()),
                        new AssistantMessage(it.getAssistantMessage())
                ))
                .flatMap(Collection::stream)
                .toList();
    }

    @Transactional
    public MessageHistory patchHistory(String id, UserMessage userMessage, AssistantMessage assistantMessage) {
        MessageHistory messageHistory = MessageHistory.builder()
                .sessionId(id)
                .userMessage(userMessage.getText())
                .assistantMessage(assistantMessage.getText())
                .build();
        return messageHistoryRepository.save(messageHistory);
    }
}
