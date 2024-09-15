package org.zapodot.testcontainers.sample.db;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

public class NamedParameterJdbcTemplateFactory {

    /**
     * Private constructor to prevent instantiation
     */
    private NamedParameterJdbcTemplateFactory() {
    }

    public static NamedParameterJdbcTemplate createNamedParameterJdbcTemplate(final DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
