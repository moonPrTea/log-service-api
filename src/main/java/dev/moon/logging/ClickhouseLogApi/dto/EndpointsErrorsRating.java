package dev.moon.logging.ClickhouseLogApi.dto;

public record EndpointsErrorsRating(
        String service,
        String endpoint,
        Integer statusCode,
        Integer countErrors,
        Double avgResponseMs,
        String firstLogDate,
        String lastLogDate
) {
}
