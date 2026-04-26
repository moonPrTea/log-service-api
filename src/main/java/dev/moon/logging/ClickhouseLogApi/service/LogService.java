package dev.moon.logging.ClickhouseLogApi.service;

import com.clickhouse.client.api.query.QueryResponse;
import dev.moon.logging.ClickhouseLogApi.dto.*;
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

  public LogShortRecord getLastCreatedServiceLog() {
    try (QueryResponse lastCreatedLog = clickhouseRepository.getLastCreatedServiceLog().join()) {
      return new ObjectMapper()
              .readValue(lastCreatedLog.getInputStream(), LogShortRecord.class);
    } catch (Exception exception) {
      LOGGER.error("Exception in getLastCreatedServiceLog: ", exception);
      return null;
    }
  }

  public List<ServiceErrorIntervals> getServiceErrorIntervals(String serviceName) {
    try (QueryResponse serviceErrorIntervals = clickhouseRepository.getServiceErrorIntervals(serviceName).join()) {
      return new ObjectMapper().readerFor(ServiceErrorIntervals.class)
              .<ServiceErrorIntervals>readValues(serviceErrorIntervals.getInputStream())
              .readAll();
    } catch (Exception exception) {
      LOGGER.error("Exception in getServiceErrorIntervals: ", exception);
      return Collections.emptyList();
    }
  }

  public HighestErrorsEndpoint getHighestErrorsEndpoint(String serviceName) {
    try (QueryResponse highestErrorsEndpoint = clickhouseRepository.getHighestErrorEndpoint(serviceName).join()) {
       var result = new ObjectMapper().readerFor(HighestErrorsEndpoint.class)
              .<HighestErrorsEndpoint>readValues(highestErrorsEndpoint.getInputStream());

       return result.hasNextValue() ? result.nextValue() : null;
    } catch (Exception exception) {
      LOGGER.error("Exception in getHighestErrorEndpoint: ", exception);
      return null;
    }
  }

  public List<MostFailingUserEndpoint> getMostFailingUserEndpoint(String serviceName) {
    try (QueryResponse mostFailingUserEndpoint = clickhouseRepository.getMostFailingUserEndpoint(serviceName).join()) {
      return new ObjectMapper()
              .readerFor(MostFailingUserEndpoint.class)
              .<MostFailingUserEndpoint>readValues(mostFailingUserEndpoint.getInputStream()).readAll();
    } catch (Exception exception) {
      LOGGER.error("Exception in getMostFailingUserEndpoint: ", exception);
      return null;
    }
  }

  public List<LogShortRecord> checkLogsByResponseTime(Integer responseTime) {
    try (QueryResponse responses = clickhouseRepository.checkLogsByResponseTime(responseTime).join()) {
      return new ObjectMapper()
              .readerFor(LogShortRecord.class)
              .<LogShortRecord>readValues(responses.getInputStream()).readAll();
    } catch (Exception exception) {
      LOGGER.error("Exception in checkResponsesByResponseTime: ", exception);
      return null;
    }
  }

  public List<ServiceStatusCodeStats> getStatusCodeStats(Integer statusCode, String serviceName) {
    try (QueryResponse responses = clickhouseRepository.getStatusCodeStats(statusCode, serviceName).join()) {
      return new ObjectMapper()
              .readerFor(ServiceStatusCodeStats.class)
              .<ServiceStatusCodeStats>readValues(responses.getInputStream()).readAll();
    } catch (Exception exception) {
      LOGGER.error("Exception in getStatusCodeStats: ", exception);
      return null;
    }
  }

  public List<FileSourceErrorsStats> getMostErrorFileSource(String serviceName) {
    try (QueryResponse responses = clickhouseRepository.getMostErrorFileSource(serviceName).join()) {
      return new ObjectMapper()
              .readerFor(FileSourceErrorsStats.class)
              .<FileSourceErrorsStats>readValues(responses.getInputStream()).readAll();
    } catch (Exception exception) {
      LOGGER.error("Exception in getMostErrorFileSource: ", exception);
      return null;
    }
  }



}
