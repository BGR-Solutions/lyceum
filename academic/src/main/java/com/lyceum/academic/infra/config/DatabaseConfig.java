package com.lyceum.academic.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration class that enables declarative transaction management for the academic module.
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {
}
