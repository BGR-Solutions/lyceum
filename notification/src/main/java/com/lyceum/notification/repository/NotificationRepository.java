package com.lyceum.notification.repository;

import com.lyceum.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing Notification entities.
 * This interface extends JpaRepository to provide CRUD operations and custom query methods for Notification entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
