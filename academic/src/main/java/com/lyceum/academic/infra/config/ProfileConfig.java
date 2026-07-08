package com.lyceum.academic.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for defining beans based on active profiles.
 * Profiles: local (development), docker (Docker Compose), cloud (production).
 */
@Configuration
public class ProfileConfig {

    @Bean
    @Profile("local")
    public String localBean() {
        return "Bean específico para ambiente local";
    }

    @Bean
    @Profile("cloud")
    public String cloudBean() {
        return "Bean específico para ambiente cloud";
    }
}
