package com.lyceum.academic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for database-related beans and settings.
 * This class can be used to define beans that are specific to certain profiles (e.g., development, production).
 */
@Configuration
public class DatabaseConfig {
    // Aqui você pode adicionar beans específicos por profile
    // Exemplo: DataSource customizado, Flyway, Liquibase etc.
}
