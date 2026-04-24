package dev.moon.logging.ClickhouseLogApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.Instant;

public record LogEvent(
        @NotNull(message = "Service name is a required field")
        String serviceName,
        String endpoint,
        HttpMethod httpMethod,
        Integer statusCode,
        Integer responseTimeMs,
        String fileSource,
        Integer userId,
        @NotNull(message = "Log level is a required field") LogLevel logLevel,
        @NotBlank(message = "Message is required") String message,
        @Null
        Instant createdAt
) {
}
