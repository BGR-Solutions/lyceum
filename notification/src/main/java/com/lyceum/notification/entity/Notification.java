package com.lyceum.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a notification entity in the system.
 * Each notification has a unique identifier, message, type, and creation timestamp.
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

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message) {
        this.message = message;
        this.type = NotificationType.INFO; // Default type
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message, NotificationType type) {
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
