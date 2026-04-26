package dev.moon.logging.ClickhouseLogApi.dto;

public record MostFailingUserEndpoint(
        String service,
        Integer userId,
        String endpoint,
        Long countErrors
) {
}
