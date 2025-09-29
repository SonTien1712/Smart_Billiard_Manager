package com.sbsms.billiardmgmt.billiardmanagement;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class FlywayMigrationIT {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("BillardManagement")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void initSystemProps() {
        // Cho Spring Boot lấy datasource từ container
        System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysql.getUsername());
        System.setProperty("spring.datasource.password", mysql.getPassword());
        System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private DataSource dataSource;

    @Test
    void migrations_apply_successfully_and_expected_tables_exist() throws Exception {
        // (Tuỳ chọn) làm sạch DB trước khi migrate
        flyway.clean();
        var result = flyway.migrate();
        assertThat(result.migrationsExecuted).isGreaterThan(0);

        try (Connection con = dataSource.getConnection();
             ResultSet rs = con.getMetaData().getTables(null, null, "Customers", null)) {
            assertThat(rs.next()).as("CUSTOMERS table should exist after migration").isTrue();
        }
    }
}
