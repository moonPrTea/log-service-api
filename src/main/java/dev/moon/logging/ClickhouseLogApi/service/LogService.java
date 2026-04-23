package dev.moon.logging.ClickhouseLogApi.service;

import dev.moon.logging.ClickhouseLogApi.dto.LogEvent;
import dev.moon.logging.ClickhouseLogApi.dto.LogShortRecord;
import dev.moon.logging.ClickhouseLogApi.repository.ClickhouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class LogService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

  @Autowired
  ClickhouseRepository clickhouseRepository;

  public void createLog(LogEvent logEventDto) {
    clickhouseRepository.createLog(logEventDto);
  }

  public boolean checkClickhouseAvailability() {
    return clickhouseRepository.checkClickhouseAvailability();
  }

  public List<LogShortRecord>  getLogsByDate(LocalDate date) {
    return clickhouseRepository.getLogsByDate(date);
  }

}
