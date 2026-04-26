package dev.moon.logging.ClickhouseLogApi.dto;

import java.util.List;

public record ServiceStatusCodeStats(
        String service,
        Integer statusCode,
        Long statusCodeErrors,
        Double avgResponseTime,
        List<String> endpoints,
        List<Integer> userIds
) {
}
