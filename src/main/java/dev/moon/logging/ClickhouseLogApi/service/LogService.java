package dev.moon.logging.ClickhouseLogApi.service;

import com.clickhouse.client.api.query.QueryResponse;
import dev.moon.logging.ClickhouseLogApi.dto.EndpointsErrorsRating;
import dev.moon.logging.ClickhouseLogApi.dto.LogEvent;
import dev.moon.logging.ClickhouseLogApi.dto.LogShortRecord;
import dev.moon.logging.ClickhouseLogApi.repository.ClickhouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class LogService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

  @Autowired
  ClickhouseRepository clickhouseRepository;

  public boolean createLog(LogEvent logEventDto) {
    return clickhouseRepository.createLog(logEventDto);
  }

  public boolean checkClickhouseAvailability() {
    return clickhouseRepository.checkClickhouseAvailability();
  }

  public List<LogShortRecord>  getLogsByDate(LocalDate date) {
    try (QueryResponse logsByDateResponse = clickhouseRepository.getLogsByDate(date).join()) {
      ObjectMapper objectMapper = new ObjectMapper();

      return objectMapper.readerFor(LogShortRecord.class)
              .<LogShortRecord>readValues(logsByDateResponse.getInputStream())
              .readAll();

    } catch (Exception exception) {
      LOGGER.error("Exception in getLogsByDate: ", exception);
      return Collections.emptyList();
    }

  }

  public List<EndpointsErrorsRating> getFiveEndpointsErrorsRating(String serviceName) {
    try (QueryResponse logsByDateResponse = clickhouseRepository.getFiveEndpointsErrorsRating(serviceName).join()) {
      ObjectMapper objectMapper = new ObjectMapper();

      return objectMapper.readerFor(EndpointsErrorsRating.class)
              .<EndpointsErrorsRating>readValues(logsByDateResponse.getInputStream())
              .readAll();

    } catch (Exception exception) {
      LOGGER.error("Exception in getFiveEndpointsErrorsRating: ", exception);
      return Collections.emptyList();
    }
  }

}
