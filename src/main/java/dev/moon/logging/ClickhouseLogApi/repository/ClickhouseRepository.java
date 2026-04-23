package dev.moon.logging.ClickhouseLogApi.repository;

import dev.moon.logging.ClickhouseLogApi.dto.LogDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class ClickhouseRepository {
  private static final Logger log = LoggerFactory.getLogger(ClickhouseRepository.class);
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
                response_time_ms UInt32,
                user_id UInt32,
                file_source String,
                log_level String,
                message String,
                created_at DateTime('UTC')
            )
            ENGINE = MergeTree
            ORDER BY (created_at, service, endpoint)
            """;

    try {
      jdbcTemplate.execute(query);
    } catch (Exception e) {
      log.error("Clickhouse server is unavailable", e);
    }
  }

  public void createLog(LogDto logDto) {
    String query = """
            INSERT INTO server_logs (
            service, endpoint, method,
            status_code, response_time_ms, 
            user_id, file_source,
            log_level, message, created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    jdbcTemplate.update(
            query,
            logDto.serviceName(),
            logDto.endpoint(),
            logDto.httpMethod().name(),
            logDto.statusCode(),
            logDto.responseTimeMs(),
            logDto.userId(),
            logDto.fileSource(),
            logDto.logLevel().name(),
            logDto.message(),
            Timestamp.from(logDto.createdAt() == null ? Instant.now() : logDto.createdAt())
    );
  }
}
