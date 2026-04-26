package dev.moon.logging.ClickhouseLogApi.resource;

import dev.moon.logging.ClickhouseLogApi.dto.*;
import dev.moon.logging.ClickhouseLogApi.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    List<EndpointsErrorsRating> endpointsErrorsRatingList = logService.getFiveEndpointsErrorsRating(serviceName);

    if (endpointsErrorsRatingList.isEmpty()) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(new ErrorAnswer("Service didn't find any endpoints errors for rating"));
    }


    return ResponseEntity
            .ok()
            .body(Map.of("rating-endpoints", endpointsErrorsRatingList));
  }

  @GetMapping("/errors_intervals")
  public ResponseEntity<?> getErrorsServiceIntervals(@RequestParam(name = "service", required = true) String serviceName) {
    List<ServiceErrorIntervals> serviceErrorIntervalsList = logService.getServiceErrorIntervals(serviceName);

    if (serviceErrorIntervalsList.isEmpty()) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(new ErrorAnswer("Service didn't find any errors service intervals"));
    }

    return ResponseEntity
            .ok()
            .body(Map.of("service", serviceName,
                    "errors_intervals", serviceErrorIntervalsList));
  }

  @GetMapping("/highest_error_endpoint")
  public ResponseEntity<?> getHighestErrorEndpoint(@RequestParam(name = "service", required = false) String serviceName) {
    HighestErrorsEndpoint highestErrorsEndpoint = logService.getHighestErrorsEndpoint(serviceName);

    if (highestErrorsEndpoint == null) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(new ErrorAnswer("Service didn't find any errors in endpoints"));
    }

    return ResponseEntity
            .ok()
            .body(Map.of("highest_error_endpoint", highestErrorsEndpoint));
  }

  @GetMapping("/most_failing_user")
  public ResponseEntity<?> getUserRatingByErrorsInEndpoints(@RequestParam(name = "service", required = true) String serviceName) {
    List<MostFailingUserEndpoint> mostFailingUserEndpoints = logService.getMostFailingUserEndpoint(serviceName);

    if (mostFailingUserEndpoints.isEmpty()) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(new ErrorAnswer("Service didn't find any errors in endpoints with user_ids"));
    }

    return ResponseEntity
            .ok()
            .body(Map.of("most_failing_user", logService.getMostFailingUserEndpoint(serviceName)));
  }

  @GetMapping("/check_response_time")
  public ResponseEntity<?> checkLogsByResponseTime(@RequestParam(name = "response_time", required = true) Integer responseTime) {
    if (responseTime < 0) {
      return ResponseEntity
              .badRequest()
              .body(new ErrorAnswer("response_time must be positive"));
    }

    List<LogShortRecord> logs = logService.checkLogsByResponseTime(responseTime);

    if (logs.isEmpty()) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(new ErrorAnswer("Service didn't find any logs with response time more than " + responseTime));
    }

    return ResponseEntity
            .ok()
            .body(Map.of("logs", logs));
  }

  @GetMapping("/status_code_stats")
  public ResponseEntity<?> getServiceStatusCodeStats(@RequestParam(name = "service", required = true) String serviceName,
                                                     @RequestParam(name="status_code", required = true) Integer statusCode) {
    if (statusCode < 300 || statusCode > 500) {
      return ResponseEntity
              .badRequest()
              .body(new ErrorAnswer("status_code must be within the range 300;500"));
    }

    return ResponseEntity
            .ok()
            .body(Map.of("logs", logService.getStatusCodeStats(statusCode, serviceName)));
  }

  @GetMapping("/most_failed_file")
  public ResponseEntity<?> getMostErrorFileSource(@RequestParam(name = "service", required = true) String serviceName) {
    return ResponseEntity
            .ok()
            .body(Map.of("logs", logService.getMostErrorFileSource(serviceName)));
  }
}
