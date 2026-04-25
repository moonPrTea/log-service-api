# log-service-api

This application:
1. consumes JSON messages from Kafka
2. stores them in ClickHouse
3. includes endpoints for logs and monitoring data ([API Routes](#api-routes))

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

### 4. Send a test JSON message to Kafka topic `test.log-create.1`
```bash
printf '%s\n' '{"serviceName":"logs-api","endpoint":"/logs","httpMethod":"POST","statusCode":500,"responseTimeMs":33,"fileSource":"LogResource.java","userId":22,"logLevel":"WARNING","message":"Code: 62, e.displayText() = DB::Exception: Syntax error: failed at position ...","createdAt":"2026-04-21T21:20:00Z"}' \
| docker exec -i log-service-api-kafka-1 /opt/kafka/bin/kafka-console-producer.sh \
--topic test.log-create.1 --bootstrap-server localhost:9092
```

```bash
printf '%s\n' '{"serviceName":"logs-api","endpoint":"/logs/stats","httpMethod":"GET","statusCode":500,"responseTimeMs":42,"fileSource":"LogResource.java","userId":11,"logLevel":"WARNING","message":"DB::Exception: Timeout exceeded: elapsed 30.001 seconds","createdAt":"2026-04-21T18:25:20Z"}' \
| docker exec -i log-service-api-kafka-1 /opt/kafka/bin/kafka-console-producer.sh \
--topic test.log-create.1 --bootstrap-server localhost:9092
```

```bash
printf '%s\n' '{"serviceName":"logs-api","endpoint":"/logs","httpMethod":"DELETE","statusCode":500,"responseTimeMs":10,"fileSource":"LogResource.java","userId":11,"logLevel":"WARNING","message":"Code: 38, DB::Exception: Cannot parse date","createdAt":"2026-04-23T13:25:20Z"}' \
| docker exec -i log-service-api-kafka-1 /opt/kafka/bin/kafka-console-producer.sh \
--topic test.log-create.1 --bootstrap-server localhost:9092
```

### 5. Verify that sent record was written to ClickHouse `server_logs` table
```bash
docker exec -it clickhouse clickhouse-client \
  --query "SELECT * FROM server_logs ORDER BY created_at DESC LIMIT 10"
```

## API Routes

### Log routes (`/log`)

| Method | Route | Query params | Description                           |
|---|---|---|---------------------------------------|
| `GET` | `/log/` | `date` (required) | returns logs for the selected date    |
| `GET` | `/log/last_created_log` | — | returns the latest created log record |

### Metrics routes (`/log/stats`)

| Method | Route | Query params               | Description                                                  |
|---|---|----------------------------|--------------------------------------------------------------|
| `GET` | `/log/stats/status` | —                          | Health check for ClickHouse availability                     |
| `GET` | `/log/stats/rating_endpoints_errors` | `service` (required param) | top endpoints with the highest number of errors for a service |
| `GET` | `/log/stats/errors_intervals` | `service` (required param) | error statistics grouped by time intervals for a service     |

### Request Examples 
1. Check Clickhouse status (`/log/stats/status`)
```bash
curl -sS "http://localhost:8080/log/stats/status"
```

2. Get rating endpoints errors for service (`/log/stats/rating_endpoints_errors`)
```bash
curl -sS "http://localhost:8080/log/stats/rating_endpoints_errors?service=logs-api"
```

3. Get stats for service error intervals (`/log/stats/errors_intervals`)
```bash
curl -sS "http://localhost:8080/log/stats/errors_intervals?service=logs-api"
```

4. Get logs by date (`/log/`)
```bash
curl -sS "http://localhost:8080/log/?date=2026-04-21"
```

5. Get last created log (`/log/last_created_log`)
```bash
curl -sS "http://localhost:8080/log/last_created_log"
```
