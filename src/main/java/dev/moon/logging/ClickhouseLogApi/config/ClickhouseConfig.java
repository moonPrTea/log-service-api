package dev.moon.logging.ClickhouseLogApi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.clickhouse.client.api.Client;


@Configuration
public class ClickhouseConfig {

  @Bean Client createClient(@Value("${db.url}") String dbUrl,
                            @Value("${db.user}") String dbUser,
                            @Value("${db.password}") String dbPassword) {
    return new Client
            .Builder().addEndpoint(dbUrl)
            .setUsername(dbUser)
            .setPassword(dbPassword)
            .setMaxConnections(50)
            .setSocketRcvbuf(500_000)
            .setSocketTcpNodelay(true)
            .build();
  }
}
