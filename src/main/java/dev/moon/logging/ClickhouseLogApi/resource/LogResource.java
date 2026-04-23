package dev.moon.logging.ClickhouseLogApi.resource;

import dev.moon.logging.ClickhouseLogApi.dto.BaseAnswer;
import dev.moon.logging.ClickhouseLogApi.dto.LogEvent;
import dev.moon.logging.ClickhouseLogApi.repository.ClickhouseRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class LogResource {
  private final ClickhouseRepository clickhouseRepository;

  public LogResource(ClickhouseRepository clickhouseRepository) {
    this.clickhouseRepository = clickhouseRepository;
  }

  @GetMapping("/status")
  public ResponseEntity<BaseAnswer> getStatus() {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(new BaseAnswer("success", "in work"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BaseAnswer> getAppLogs(@PathVariable Integer id) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(new BaseAnswer("success", "in work"));
  }

  @PostMapping
  public ResponseEntity<BaseAnswer> createLogRecord(@Valid @RequestBody LogEvent logDto) {
    clickhouseRepository.createLog(logDto);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new BaseAnswer("success", "log created"));
  }
}
