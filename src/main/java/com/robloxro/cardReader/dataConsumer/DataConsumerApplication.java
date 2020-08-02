package com.robloxro.cardReader.dataConsumer;

import com.robloxro.cardReader.dataConsumer.flyway.FlywayInitialiser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataConsumerApplication {

	public static void main(String[] args) {

		SpringApplication.run(DataConsumerApplication.class, args);
		FlywayInitialiser.runMigrations();

	}



}
