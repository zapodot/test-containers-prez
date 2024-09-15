package org.zapodot.testcontainers.sample.db;

public record DataSourceConfig(String jdbcUrl, String jdbcUser, String jdcPassword) {
}
