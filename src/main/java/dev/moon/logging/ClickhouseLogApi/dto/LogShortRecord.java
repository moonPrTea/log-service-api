package dev.moon.logging.ClickhouseLogApi.dto;

public record LogShortRecord(
        String service,
        String method,
        String endpoint,
        Integer statusCode,
        Double responseTimeMs,
        String fileSource,
        int userId,
        String message,
        String createdAt
) {
}
