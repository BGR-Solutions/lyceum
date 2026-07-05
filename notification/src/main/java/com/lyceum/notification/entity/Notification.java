package com.lyceum.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a notification stored in the system.
 * Each notification has a message, a type, and a creation timestamp.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message) {
        this.message = message;
        this.type = NotificationType.INFO;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message, NotificationType type) {
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
