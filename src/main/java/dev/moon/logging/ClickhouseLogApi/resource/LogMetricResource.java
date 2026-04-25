package dev.moon.logging.ClickhouseLogApi.resource;

import dev.moon.logging.ClickhouseLogApi.dto.BaseAnswer;
import dev.moon.logging.ClickhouseLogApi.dto.ErrorAnswer;
import dev.moon.logging.ClickhouseLogApi.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/log/stats")
public class LogMetricResource {

  @Autowired
  LogService logService;

  @GetMapping("/status")
  public ResponseEntity<?> getStatus() {
    Boolean clickhouseAvailability = logService.checkClickhouseAvailability();

    if (!clickhouseAvailability) {
      return ResponseEntity
              .status(HttpStatus.BAD_GATEWAY)
              .body(new ErrorAnswer("Clickhouse server isn't available"));
    }
    return ResponseEntity
            .ok(new BaseAnswer("success", "Clickhouse server is working"));
  }

  @GetMapping("/rating_endpoints_errors")
  public ResponseEntity<?> getEndpointsRatingByErrors(@RequestParam(name = "service", required = true) String serviceName) {
    return ResponseEntity
            .ok()
            .body(Map.of("rating-endpoints", logService.getFiveEndpointsErrorsRating(serviceName)));
  }

  @GetMapping("/errors_intervals")
  public ResponseEntity<?> getErrorsServiceIntervals(@RequestParam(name = "service", required = true) String serviceName) {
    return ResponseEntity
            .ok()
            .body(Map.of("service", serviceName,
                    "errors_intervals", logService.getServiceErrorIntervals(serviceName)));
  }

}
