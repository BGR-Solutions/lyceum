package com.lyceum.notification.messaging;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.entity.ProcessedEvent;
import com.lyceum.notification.repository.ProcessedEventRepository;
import com.lyceum.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EnrollmentEventListener {

    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;

    public EnrollmentEventListener(NotificationService notificationService,
                                   ProcessedEventRepository processedEventRepository) {
        this.notificationService = notificationService;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @RabbitListener(queues = "enrollment.events")
    public void handleEnrollmentEvent(EnrollmentEventMessage message) {
        if (message.eventId() == null || processedEventRepository.existsById(message.eventId())) {
            return;
        }

        Notification notification = new Notification(
                "Evento " + message.eventType() + " para matrícula " + message.enrollmentId()
        );
        notificationService.sendNotification(notification);
        processedEventRepository.save(new ProcessedEvent(message.eventId()));
    }
}
