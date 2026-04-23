package dev.moon.logging.ClickhouseLogApi.dto;

import java.time.Instant;

public record EndpointsErrorsRating(
        String service,
        String endpoint,
        Integer statusCode,
        Integer countErrors,
        Double avgResponseMs,
        Instant firstLogDate,
        Instant lastLogDate
) {
}
