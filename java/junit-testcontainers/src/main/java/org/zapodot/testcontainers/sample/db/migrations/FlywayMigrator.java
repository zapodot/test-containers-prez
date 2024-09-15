package org.zapodot.testcontainers.sample.db.migrations;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.testcontainers.sample.db.DataSourceConfig;

public class FlywayMigrator {
    private final Logger LOGGER = LoggerFactory.getLogger(FlywayMigrator.class);
    private final FluentConfiguration configuration = Flyway.configure()
            .installedBy("zapodot")
            .loggers("slf4j")
            .locations("classpath:/migrations");

    public void migrate(DataSourceConfig dataSourceConfig) {
        configuration.dataSource(
                dataSourceConfig.jdbcUrl(),
                dataSourceConfig.jdbcUser(),
                dataSourceConfig.jdcPassword());
        Flyway flyway = configuration.load();
        var migrationResults = flyway.migrate();
        LOGGER.info("Successfully applied {} migrations", migrationResults.migrationsExecuted);
    }
}
