package com.engineering_lab.hunger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.engineering_lab.hunger.tenant.api.CreateTenantUseCase;
import com.engineering_lab.hunger.tenant.application.command.CreateTenantCommand;
import com.engineering_lab.hunger.tenant.application.result.CreatedTenant;

  @Testcontainers
  @SpringBootTest
  @ActiveProfiles("test")
  class HungerApplicationTests {

	private static final UUID USER_ID = UUID.fromString(
			"01890f9a-6b7c-7def-8123-456789abcdef"
	);

	private static final DockerImageName POSTGRES_IMAGE =
			DockerImageName.parse("postgres:18");

	@Container
	@ServiceConnection
	static final PostgreSQLContainer postgres =
			new PostgreSQLContainer(POSTGRES_IMAGE)
					.withDatabaseName("hunger")
					.withUsername("postgres")
					.withPassword("postgres");

	@Autowired
	CreateTenantUseCase createTenantUseCase;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	Environment environment;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void createsTenantAndOwnerMembershipInPostgres() {
		String schema = environment.getRequiredProperty(
				"spring.jpa.properties.hibernate.default_schema"
		);
		Instant now = Instant.parse("2026-07-19T08:00:00Z");
		jdbcTemplate.update(
				"""
				INSERT INTO %s.users (
				    user_id, name, email, normalized_email, password_hash,
				    email_verified_at, created_at, updated_at
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""".formatted(schema),
				USER_ID,
				"Tenant Creator",
				"creator@example.com",
				"creator@example.com",
				"test-password-hash",
				Timestamp.from(now),
				Timestamp.from(now),
				Timestamp.from(now)
		);

		CreatedTenant tenant = createTenantUseCase.execute(new CreateTenantCommand(
				USER_ID,
				"Engineering Lab",
				"LAB01"
		));

		assertEquals(7, tenant.tenantId().version());
		assertEquals(
				"OWNER",
				jdbcTemplate.queryForObject(
						"SELECT role FROM %s.membership WHERE tenant_id = ? AND user_id = ?"
								.formatted(schema),
						String.class,
						tenant.tenantId(),
						USER_ID
				)
		);
	}
  }
