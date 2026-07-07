package com.lyceum.notification.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservabilityConfigTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private ObservabilityConfig filter;

    // --- Regra: gera traceId quando cabeçalho está ausente ---

    @Test
    void generatesTraceIdWhenHeaderIsAbsent() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("X-Correlation-Id"), captor.capture());
        assertNotNull(captor.getValue());
        assertFalse(captor.getValue().isBlank());
    }

    @Test
    void generatesNewTraceIdWhenHeaderIsBlank() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn("   ");

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("X-Correlation-Id"), captor.capture());
        assertFalse(captor.getValue().isBlank());
        assertNotEquals("   ", captor.getValue());
    }

    // --- Regra: propaga o traceId existente ---

    @Test
    void propagatesExistingTraceId() throws Exception {
        String existingTraceId = "my-trace-id-123";
        when(request.getHeader("X-Correlation-Id")).thenReturn(existingTraceId);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader("X-Correlation-Id", existingTraceId);
    }

    // --- Regra: a cadeia de filtros deve sempre ser chamada ---

    @Test
    void alwaysContinuesFilterChain() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void continuesFilterChainWhenTraceIdProvided() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn("provided-id");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    // --- Regra: MDC deve ser limpo após processamento ---

    @Test
    void mdcIsClearedAfterFilterExecution() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(MDC.get("traceId"));
    }

    @Test
    void mdcIsClearedEvenWhenFilterChainThrows() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn("trace-id");
        doThrow(new RuntimeException("chain error")).when(filterChain).doFilter(request, response);

        assertThrows(RuntimeException.class, () -> filter.doFilterInternal(request, response, filterChain));

        assertNull(MDC.get("traceId"));
    }
}
