package org.zapodot.testcontainers.sample.db;

import org.postgresql.Driver;
import org.slf4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DataSourceFactory.class);
    public static final String DRIVER_CLASS_NAME = Driver.class.getName();

    /**
     * Private constructor to prevent instantiation
     */
    private DataSourceFactory() {
    }

    public static DataSource createDataSource(final DataSourceConfig dataSourceConfig) {
        LOGGER.info("Creating DataSource with jdbcUrl: {} and driver {}", dataSourceConfig.jdbcUrl(), DRIVER_CLASS_NAME);
        return new SingleConnectionDataSource(
                dataSourceConfig.jdbcUrl(),
                dataSourceConfig.jdbcUser(),
                dataSourceConfig.jdcPassword(),
                true);

    }
}
