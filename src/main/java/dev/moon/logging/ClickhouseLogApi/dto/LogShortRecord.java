package dev.moon.logging.ClickhouseLogApi.dto;

import java.time.Instant;

public record LogShortRecord(
        String serviceName,
        String method,
        String endpoint,
        Integer statusCode,
        int userId,
        String message,
        Instant createdAt
) {
}
