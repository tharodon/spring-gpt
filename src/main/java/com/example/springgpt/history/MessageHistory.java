package com.example.springgpt.history;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "message_history", schema = "public")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MessageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sessionId;

    private String message;

    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
