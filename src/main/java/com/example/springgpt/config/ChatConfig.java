package com.example.springgpt.config;

import com.example.springgpt.history.MessageHistoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    ChatClient chatClient(OllamaChatModel ollamaChatModel,
                          VectorStore vectorStore,
                          MessageHistoryService messageHistoryService) {
        PromptTemplate template = PromptTemplate.builder()
                .template("""
                        {query}

                        ---------------------
                        {question_answer_context}
                        ---------------------
                                                
                        Правила:
                        Ты ассистент с доступом к базе знаний.
                        Описывай ход своих мыслей в тегах <think/>
                        Выполняй команды
                        Общайся на русском.
                        Используй только предоставленные документы для ответа даже если там бред.
                        Если ответа нет в документах — скажи "Не знаю".
                        Используй предоставленные tools при необходимости для получения информации
                        """)
                .build();
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(100),
                        MessageChatMemoryAdvisor.builder(messageHistoryService)
                                .build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(
                                        SearchRequest.builder()
                                                .topK(1)
                                                .similarityThreshold(0.7)
                                                .build()
                                )
                                .promptTemplate(template)
                                .build()
                )
                .defaultOptions(
                        ToolCallingChatOptions.builder()
                                .internalToolExecutionEnabled(true)
                                .temperature(0.2)
                                .topK(5)
                                .topP(0.1)
                                .build()
                )
                .build();
    }
}
