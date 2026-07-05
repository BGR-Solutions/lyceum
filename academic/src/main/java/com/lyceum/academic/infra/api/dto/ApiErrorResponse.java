package com.lyceum.academic.infra.api.dto;

import java.time.Instant;

/**
 * Immutable record representing a standardized API error response.
 * Returned by the global exception handler when an error occurs.
 */
public record ApiErrorResponse(String message, String error, Instant timestamp) {
}
