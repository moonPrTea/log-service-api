package dev.moon.logging.ClickhouseLogApi.dto;

public record HighestErrorsEndpoint(
        String service,
        String endpoint,
        Long countErrors,
        String firstErrorTime,
        String lastErrorTime
) {
}
