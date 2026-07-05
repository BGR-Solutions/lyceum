package com.lyceum.academic.infra.api.dto;

import java.time.Instant;

public record ApiErrorResponse(String message, String error, Instant timestamp) {
}
