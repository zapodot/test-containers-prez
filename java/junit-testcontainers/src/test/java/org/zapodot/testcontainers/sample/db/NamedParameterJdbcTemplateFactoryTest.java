package org.zapodot.testcontainers.sample.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NamedParameterJdbcTemplateFactoryTest {

    @DisplayName("Skal lage en NamedParameterJdbcTemplate")
    @Test
    void createNamedParameterJdbcTemplate(@Mock DataSource dataSource) {
        assertThat(NamedParameterJdbcTemplateFactory.createNamedParameterJdbcTemplate(dataSource))
                .isNotNull();
    }
}