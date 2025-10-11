package org.project.shopservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestContainersConfig {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgreSQLContainer(){
		return new PostgreSQLContainer<>("postgres:latest");
	}

	@Bean
	@ServiceConnection
	public MongoDBContainer mongoDBContainer(){
		return new MongoDBContainer("mongo:latest");
	}

}
