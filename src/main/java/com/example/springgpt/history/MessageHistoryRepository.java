package com.example.springgpt.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Integer> {

    List<MessageHistory> findAllBySessionIdOrderByCreatedAt(String sessionId);
}
