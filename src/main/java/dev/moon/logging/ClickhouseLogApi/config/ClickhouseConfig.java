package dev.moon.logging.ClickhouseLogApi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
public class ClickhouseConfig {

  @Bean
  public DataSource setUpConnection(@Value("${db.url}") String dbUrl,
                                    @Value("${db.user}") String dbUser,
                                    @Value("${db.password}") String dbPassword) {
    DriverManagerDataSource driverManager = new DriverManagerDataSource();
    driverManager.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");

    driverManager.setUrl(dbUrl);
    driverManager.setUsername(dbUser);
    driverManager.setPassword(dbPassword);

    return driverManager;
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

}
