package dev.moon.logging.ClickhouseLogApi.resource;

import dev.moon.logging.ClickhouseLogApi.dto.ErrorAnswer;
import dev.moon.logging.ClickhouseLogApi.dto.LogShortRecord;
import dev.moon.logging.ClickhouseLogApi.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/log")
public class LogResource {

  @Autowired
  LogService logService;

  @GetMapping("/")
  public ResponseEntity<?> getAppLogsByDate(@RequestParam(name = "date", required = true) LocalDate logsDate) {
    List<LogShortRecord> logsByDate = logService.getLogsByDate(logsDate);

    if (logsByDate.isEmpty()) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(new ErrorAnswer("No logs were found for date " + logsDate));
    }

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(Map.of("logs", logsByDate));
  }

  @GetMapping("/last_created_log")
  public ResponseEntity<?> getLastCreatedServiceLog() {
    LogShortRecord lastCreatedServiceLog = logService.getLastCreatedServiceLog();

    if (lastCreatedServiceLog == null) {
      return ResponseEntity
              .notFound()
              .build();
    }

    return ResponseEntity
            .ok()
            .body(Map.of("last_created_log", lastCreatedServiceLog));
  }
}
