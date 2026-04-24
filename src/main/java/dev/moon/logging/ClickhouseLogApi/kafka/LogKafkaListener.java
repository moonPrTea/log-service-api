package dev.moon.logging.ClickhouseLogApi.kafka;


import dev.moon.logging.ClickhouseLogApi.dto.LogEvent;
import dev.moon.logging.ClickhouseLogApi.repository.ClickhouseRepository;
import dev.moon.logging.ClickhouseLogApi.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
public class LogKafkaListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogKafkaListener.class);
  private final ClickhouseRepository clickhouseRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  LogService logService;

  public LogKafkaListener(ClickhouseRepository clickhouseRepository, ObjectMapper objectMapper) {
    this.clickhouseRepository = clickhouseRepository;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(groupId = "${kafka.first.group}", topics = "${kafka.topic.logs}")
  public void listen(String value, Acknowledgment ack) {
    LOGGER.info("Got message from kafka: {}", value);

    try {
      LogEvent logDto = objectMapper.readValue(value, LogEvent.class);
      logService.createLog(logDto);
      ack.acknowledge();

      LOGGER.info("Kafka message saved to ClickHouse");
    } catch (Exception e) {
      LOGGER.warn("Kafka message processing failed. Message, error {}", value, e);
      ack.acknowledge();
    }
  }

}
