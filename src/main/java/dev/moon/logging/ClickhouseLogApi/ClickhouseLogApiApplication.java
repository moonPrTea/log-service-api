package dev.moon.logging.ClickhouseLogApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ClickhouseLogApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClickhouseLogApiApplication.class, args);
	}

}
