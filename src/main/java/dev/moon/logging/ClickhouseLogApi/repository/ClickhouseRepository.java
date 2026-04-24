package dev.moon.logging.ClickhouseLogApi.repository;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.query.QueryResponse;
import dev.moon.logging.ClickhouseLogApi.dto.LogEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Repository
public class ClickhouseRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClickhouseRepository.class);

  @Autowired
  Client client;

  @PostConstruct
  public void initTable() {
    String query = """
            CREATE TABLE IF NOT EXISTS server_logs
            (
                service String,
                endpoint String,
                method String,
                status_code UInt16,
                message String,
                file_source String,
                response_time_ms UInt32,
                user_id UInt32,
                log_level String,
                created_at DateTime64(3, 'UTC')
            )
            ENGINE = MergeTree
            ORDER BY (created_at, service, endpoint)
            """;

    try (QueryResponse response = client.query(query).join()){
    } catch (Exception e) {
      LOGGER.error("Create table operation didn't execute: ", e);
    }
  }

  public boolean checkClickhouseAvailability() {
    try (QueryResponse response = client.query("SELECT 5").get()) {
      return true;
    } catch (Exception exception) {
      LOGGER.error("Clickhouse is unavailable: ", exception.getMessage());
      return false;
    }
  }

  public CompletableFuture<QueryResponse> getLogsByDate(LocalDate date) {
    String query = """
            SELECT
                service, method, endpoint,
                status_code as statusCode, message,
                user_id as userId, created_at as createdAt
            FROM server_logs
            WHERE toDate(created_at) = {createdAt:Date}
            FORMAT JSONEachRow
            """;
    Map<String, Object> queryParams = Map.of("createdAt", date);

    return client.query(query, queryParams);
  }


  public boolean createLog(LogEvent logEvent) {
    String query = """
            INSERT INTO server_logs (
            service, endpoint, method,
            status_code, message, file_source,
            response_time_ms, user_id,
            log_level, created_at
            )
            VALUES (
            {service:String}, {endpoint:String},
            {method:String}, {statusCode:Integer},
            {message:String}, {fileSource: String},
            {responseTimeMs:Double}, {userId:Integer},
            {logLevel:String}, {createdAt:DateTime(3)}
            )
            """;

    Map<String, Object> queryParams = Map.of(
            "service", logEvent.serviceName(),
            "endpoint", logEvent.endpoint(),
            "method", logEvent.httpMethod(),
            "statusCode", logEvent.statusCode(),
            "message", logEvent.message(),
            "fileSource", logEvent.fileSource(),
            "responseTimeMs", logEvent.responseTimeMs(),
            "userId", logEvent.userId(),
            "logLevel", logEvent.logLevel(),
            "createdAt", Timestamp.from(logEvent.createdAt() == null ? Instant.now() : logEvent.createdAt())
            );

    try (QueryResponse response = client.query(query, queryParams).get()) {
      return true;
    } catch (Exception e) {
      LOGGER.error("Insert into server_logs operation didn't execute: ", e);
      return false;
    }
  }

  public CompletableFuture<QueryResponse> getFiveEndpointsErrorsRating(String serviceName) {
    String query = """
            select service, endpoint, status_code as statusCode,
                   count(*) as countErrors,
                   avg(response_time_ms) as avgResponseMs,
                   min(created_at) as firstLogDate,
                   max(created_at) as lastLogDate
            from server_logs
            where service = {serviceName:String}
            group by service, endpoint, status_code
            order by countErrors desc
            limit 5
            FORMAT JSONEachRow
            """;
    Map<String, Object> queryParams = Map.of("serviceName", serviceName);

    return client.query(
            query,
            queryParams
    );
  }
}
