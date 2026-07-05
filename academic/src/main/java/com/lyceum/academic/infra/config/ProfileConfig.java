package com.lyceum.academic.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for defining beans based on active profiles (on the application.yml).
 * This class allows the application to load different beans depending on the environment (e.g., local, AWS).
 */
@Configuration
public class ProfileConfig {

    @Bean
    @Profile("local")
    public String localBean() {
        return "Bean específico para ambiente local";
    }

    @Bean
    @Profile("aws")
    public String awsBean() {
        return "Bean específico para ambiente AWS";
    }
}
