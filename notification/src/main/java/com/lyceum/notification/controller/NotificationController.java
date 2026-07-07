package com.lyceum.notification.controller;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing notifications.
 * This controller provides endpoints to retrieve and manage notifications.
 */
@Tag(name = "Notifications", description = "Consulta do histórico de notificações geradas a partir de eventos de matrícula")
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Operation(summary = "Listar todas as notificações",
            description = "Retorna o histórico completo de notificações geradas pelo serviço.")
    @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class))))
    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Operation(summary = "Buscar notificação por ID",
            description = "Retorna uma notificação específica pelo seu identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificação encontrada",
                    content = @Content(schema = @Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "500", description = "Notificação não encontrada",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public Notification getNotification(
            @Parameter(description = "ID da notificação", required = true) @PathVariable UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}
