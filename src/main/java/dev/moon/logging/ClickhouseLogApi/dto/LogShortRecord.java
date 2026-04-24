package dev.moon.logging.ClickhouseLogApi.dto;

public record LogShortRecord(
        String service,
        String method,
        String endpoint,
        Integer statusCode,
        int userId,
        String message,
        String createdAt
) {
}
