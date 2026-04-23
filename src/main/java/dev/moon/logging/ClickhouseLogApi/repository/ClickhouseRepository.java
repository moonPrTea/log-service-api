package dev.moon.logging.ClickhouseLogApi.repository;

import dev.moon.logging.ClickhouseLogApi.dto.LogEvent;
import dev.moon.logging.ClickhouseLogApi.dto.LogShortRecord;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ClickhouseRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClickhouseRepository.class);
  private final JdbcTemplate jdbcTemplate;

  public ClickhouseRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

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

    try {
      jdbcTemplate.execute(query);
    } catch (Exception e) {
      LOGGER.error("Clickhouse server is unavailable", e);
    }
  }

  public boolean checkClickhouseAvailability() {
    try {
      return jdbcTemplate.queryForObject(
              "SELECT 5", Integer.class) != null;
    } catch (Exception exception) {
      LOGGER.error("Clickhouse is unavailable: ", exception.getMessage());
      return false;
    }
  }

  public List<LogShortRecord> getLogsByDate(LocalDate date) {

    String query = """
            SELECT
                service, method, endpoint,
                status_code, message,
                user_id, created_at
            FROM server_logs
            WHERE toDate(created_at) = ?
            """;

    return jdbcTemplate.query(
            query,
            (record, index) -> new LogShortRecord(
                    record.getString("service"),
                    record.getString("method"),
                    record.getString("endpoint"),
                    record.getInt("status_code"),
                    record.getInt("user_id"),
                    record.getString("message"),
                    record.getTimestamp("created_at").toInstant()
            ),
            date
    );
  }


  public void createLog(LogEvent logEvent) {
    String query = """
            INSERT INTO server_logs (
            service, endpoint, method,
            status_code, message, file_source,
            response_time_ms, user_id,
            log_level, created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    jdbcTemplate.update(
            query,
            logEvent.serviceName(),
            logEvent.endpoint(),
            logEvent.httpMethod().name(),
            logEvent.statusCode(),
            logEvent.message(),
            logEvent.fileSource(),
            logEvent.responseTimeMs(),
            logEvent.userId(),
            logEvent.logLevel().name(),
            Timestamp.from(logEvent.createdAt() == null ? Instant.now() : logEvent.createdAt())
    );
  }
}
