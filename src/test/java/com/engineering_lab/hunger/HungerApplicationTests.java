package com.engineering_lab.hunger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

  @Testcontainers
  @SpringBootTest
  @ActiveProfiles("test")
  class HungerApplicationTests {

	private static final DockerImageName POSTGRES_IMAGE =
			DockerImageName.parse("postgres:18");

	@Container
	@ServiceConnection
	static final PostgreSQLContainer postgres =
			new PostgreSQLContainer(POSTGRES_IMAGE)
					.withDatabaseName("hunger")
					.withUsername("postgres")
					.withPassword("postgres");

	@Test
	void contextLoads() {
	}
  }

