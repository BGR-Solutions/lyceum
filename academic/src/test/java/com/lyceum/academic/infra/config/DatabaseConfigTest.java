package com.lyceum.academic.infra.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConfigTest {

    @Test
    void databaseConfigCanBeInstantiated() {
        DatabaseConfig config = new DatabaseConfig();

        assertNotNull(config);
    }
}
