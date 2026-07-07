package com.lyceum.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI/Swagger metadata for the notification service.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notificationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .description("Serviço responsável pelo consumo de eventos de matrícula e envio de notificações. " +
                                "Oferece endpoints para consulta do histórico de notificações geradas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Lyceum")
                                .url("https://github.com/lyceum")));
    }
}
