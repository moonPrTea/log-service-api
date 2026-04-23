# log-service-api

This application:
1. consumes JSON messages from Kafka
2. stores them in ClickHouse

## Configuration
Before running the application, configure required properties in 
```application.properties```:
1. `kafka.servers`
2. `kafka.first.group`
3. `kafka.topic.logs`
4. `db.url`
5. `db.user`
6. `db.password`

## Quick Start
### 1. Start infrastructure (Kafka and ClickHouse)
```bash
docker compose up -d
```

### 2. (Optional) Create another topic:
Replace ```<topic-name>``` with chosen name
```bash
docker exec -it log-service-api-kafka-1 /opt/kafka/bin/kafka-topics.sh \
  --create --topic <topic-name> \
  --bootstrap-server localhost:9092 \
  --partitions 1 --replication-factor 1
```

### 3. Run the application

```bash
mvn spring-boot:run
```

### 4. Send a test JSON message to Kafka topic `logs-topic`
```bash
printf '%s\n' '{"serviceName":"logs-api","endpoint":"/logs","httpMethod":"POST","statusCode":500,"responseTimeMs":33,"fileSource":"LogResource.java","userId":22,"logLevel":"WARNING","message":"Code: 62, e.displayText() = DB::Exception: Syntax error: failed at position ...","createdAt":"2026-04-21T21:20:00Z"}' \
| docker exec -i log-service-api-kafka-1 /opt/kafka/bin/kafka-console-producer.sh \
--topic test.log-create.1 --bootstrap-server localhost:9092
```

### 5. Verify that sent record was written to ClickHouse `server_logs` table
```bash
docker exec -it clickhouse clickhouse-client \
  --query "SELECT * FROM server_logs ORDER BY created_at DESC LIMIT 10"
```
