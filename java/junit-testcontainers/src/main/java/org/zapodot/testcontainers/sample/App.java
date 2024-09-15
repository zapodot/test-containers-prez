package org.zapodot.testcontainers.sample;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.zapodot.testcontainers.sample.csv.CsvReader;
import org.zapodot.testcontainers.sample.db.DataSourceConfig;
import org.zapodot.testcontainers.sample.db.DataSourceFactory;
import org.zapodot.testcontainers.sample.db.migrations.FlywayMigrator;
import org.zapodot.testcontainers.sample.model.Role;
import org.zapodot.testcontainers.sample.repositories.read.RoleReadRepository;
import org.zapodot.testcontainers.sample.repositories.write.UserWriteRepository;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.zapodot.testcontainers.sample.db.NamedParameterJdbcTemplateFactory.createNamedParameterJdbcTemplate;

public class App {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(App.class);

    // This class is not meant to be instantiated
    private App() {
    }

    public static void main(String[] args) {
        var appArgs = parseArgs(args);
        DataSourceConfig dataSourceConfig = new DataSourceConfig(
                appArgs.getJdbcUrl(),
                appArgs.getJdbcUser(),
                appArgs.getJdbcPassword());
        final DataSource dataSource = DataSourceFactory.createDataSource(
                dataSourceConfig);
        LOGGER.info("Migrerer databaseskjema om nødvendig");
        migrateDatabaseIfNeeded(dataSourceConfig);
        final var allRoles = StreamSupport.stream(new RoleReadRepository(createNamedParameterJdbcTemplate(dataSource)).findAll().spliterator(), false)
                .collect(Collectors.toMap(role -> role.name().toLowerCase(), Role::id));
        LOGGER.info("Tilgjengelige roller: {}", allRoles.keySet());
        UserWriteRepository userWriteRepository = new UserWriteRepository(createNamedParameterJdbcTemplate(dataSource));
        TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        transactionTemplate.afterPropertiesSet();
        transactionTemplate.execute(status -> {
            try {
                CsvReader.readFromFile(appArgs.getInputFile())
                        .forEach(userWithRoles ->
                                userWriteRepository.save(
                                        userWithRoles.name(),
                                        userWithRoles.roles().stream()
                                                .map(key -> Objects.requireNonNull(
                                                        allRoles.get(key.trim().toLowerCase()), "Fant ingen rolle som heter " + key))
                                                .collect(Collectors.toList())));
            } catch (RuntimeException e) {
                LOGGER.error("Feil under lagring av brukere. Ruller tilbake", e);
                status.setRollbackOnly();
            }
            return null;
        });
    }

    private static void migrateDatabaseIfNeeded(DataSourceConfig dataSourceConfig) {
        FlywayMigrator flywayMigrator = new FlywayMigrator();
        flywayMigrator.migrate(dataSourceConfig);
    }

    private static AppArgs parseArgs(String[] args) {
        var appArgs = new AppArgs();
        JCommander.newBuilder()
                .addObject(appArgs)
                .acceptUnknownOptions(true)
                .programName("junit-testcontainers-sample")
                .build()
                .parse(args);
        return appArgs;
    }


}
