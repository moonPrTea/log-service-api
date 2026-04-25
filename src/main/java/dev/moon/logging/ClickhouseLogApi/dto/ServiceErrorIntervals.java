package dev.moon.logging.ClickhouseLogApi.dto;

import java.util.List;

public record ServiceErrorIntervals(
        String timeInterval,
        String statusCodeGroup,
        List<String> highestErrorEndpoint,
        Long countErrors,
        List<Integer> intervalStatusCodes,
        String percentErrorsInInterval,
        Double avgResponseTime,
        String firstErrorTime,
        String lastErrorTime

) {
}
