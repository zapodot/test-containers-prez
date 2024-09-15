package org.zapodot.testcontainers.sample.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DataSourceFactoryTest {

    @DisplayName("Skal lage en DataSource")
    @Test
    void createDataSource() {
        assertThat(DataSourceFactory.createDataSource(
                new DataSourceConfig("jdbc:postgresql://localhost:5432/test",
                        "test",
                        "test")))
                .satisfies(dataSource -> {
                    assertNotNull(dataSource);
                    assertThat(dataSource).isInstanceOf(SingleConnectionDataSource
                            .class);
                });
    }

}