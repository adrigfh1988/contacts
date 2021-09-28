package com.example.contactspost.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class PostgresIntegrationSetup {

	// will be started before and stopped after each test method
	@Container
	protected static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres")
			.withDatabaseName("postgres").withUsername("postgres").withPassword("example");

	@DynamicPropertySource
	public static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresqlContainer::getUsername);
		registry.add("spring.datasource.password", postgresqlContainer::getPassword);
	}

	@BeforeAll
	static void setup() {
		postgresqlContainer.start();
	}

	@AfterAll
	static void tearDown() {
		postgresqlContainer.stop();
	}

}
