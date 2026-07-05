package com.lyceum.notification.repository;

import com.lyceum.notification.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for tracking processed enrollment event IDs.
 * Used to ensure idempotent event handling in the notification module.
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
}
