package dev.moon.logging.ClickhouseLogApi.dto;

import java.util.List;

public record FileSourceErrorsStats(
        String fileSource,
        Long countFileErrors,
        List<String> endpoints,
        Double avgResponseTime,
        Long count500errors,
        Long uniqueUsers
) {
}
