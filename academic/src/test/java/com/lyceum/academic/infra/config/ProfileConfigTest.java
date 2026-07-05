package com.lyceum.academic.infra.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileConfigTest {

    @Test
    void localBeanReturnsExpectedValue() {
        ProfileConfig config = new ProfileConfig();

        assertEquals("Bean específico para ambiente local", config.localBean());
    }

    @Test
    void awsBeanReturnsExpectedValue() {
        ProfileConfig config = new ProfileConfig();

        assertEquals("Bean específico para ambiente AWS", config.awsBean());
    }
}
