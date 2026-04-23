package dev.moon.logging.ClickhouseLogApi.resource;

import dev.moon.logging.ClickhouseLogApi.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/log")
public class LogResource {

  @Autowired
  LogService logService;

  @GetMapping("/")
  public ResponseEntity<?> getAppLogsByDate(@RequestParam(name = "date", required = true) LocalDate logsDate,
                                            @RequestParam(name = "service", required = false) String serviceName) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(Map.of("logs", logService.getLogsByDate(logsDate)));
  }
}
