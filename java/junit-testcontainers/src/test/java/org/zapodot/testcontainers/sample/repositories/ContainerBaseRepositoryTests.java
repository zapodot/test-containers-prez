package org.zapodot.testcontainers.sample.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.zapodot.testcontainers.sample.db.DataSourceConfig;
import org.zapodot.testcontainers.sample.db.DataSourceFactory;
import org.zapodot.testcontainers.sample.db.NamedParameterJdbcTemplateFactory;
import org.zapodot.testcontainers.sample.db.migrations.FlywayMigrator;
import org.zapodot.testcontainers.sample.model.Role;
import org.zapodot.testcontainers.sample.repositories.read.RoleReadRepository;
import org.zapodot.testcontainers.sample.repositories.read.UserReadRepository;
import org.zapodot.testcontainers.sample.repositories.write.RoleWriteRepository;
import org.zapodot.testcontainers.sample.repositories.write.UserWriteRepository;

import javax.sql.DataSource;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("Tester databasekode med testcontainer")
@Testcontainers(disabledWithoutDocker = true)
public class ContainerBaseRepositoryTests {

    @Container
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:12.20-alpine");

    @BeforeEach
    void setUp() {
        new FlywayMigrator().migrate(new DataSourceConfig(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()));
    }

    @DisplayName("Kan opprette en DataSource og koble til databasen")
    @Test
    void createDatasourceTest() {
        var dataSource = createDataSource();
        try (var connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getDatabaseProductName())
                    .isEqualTo("PostgreSQL");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("Roller")
    @Nested
    class RolleTest {

        @DisplayName("lagre og deretter hente")
        @Test
        void saveFetchAndDelete() {
            var dataSource = createDataSource();
            var roleWriteRepository = new RoleWriteRepository(
                    NamedParameterJdbcTemplateFactory.createNamedParameterJdbcTemplate(dataSource));
            var roleReadRepository = new RoleReadRepository(
                    NamedParameterJdbcTemplateFactory.createNamedParameterJdbcTemplate(dataSource));
            var rolle = roleWriteRepository.save("Testrolle");
            assertThat(rolle).isEqualTo(roleReadRepository.findById(rolle.id()));
            roleWriteRepository.delete(rolle.id());
        }

        @DisplayName("finn alle roller")
        @Test
        void finnAlle() {
            var dataSource = createDataSource();
            var roleWriteRepository = new RoleWriteRepository(
                    NamedParameterJdbcTemplateFactory.createNamedParameterJdbcTemplate(dataSource));
            var roleReadRepository = new RoleReadRepository(
                    NamedParameterJdbcTemplateFactory.createNamedParameterJdbcTemplate(dataSource));
            final Role rolle1 = roleWriteRepository.save("Testrolle1");
            final Role rolle2 = roleWriteRepository.save("Testrolle2");
            Iterable<Role> alleRoller = roleReadRepository.findAll();
            assertThat(alleRoller).isNotEmpty();
            assertThat(alleRoller).contains(rolle1, rolle2);
            roleWriteRepository.delete(rolle1.id());
            roleWriteRepository.delete(rolle2.id());
        }
    }

    @DisplayName("Brukere")
    @Nested
    class BrukerTest {

        @DisplayName("lagre og deretter hente")
        @Test
        void saveFetchAndDelete() {
            final var dataSource = createDataSource();
            final var namedJdbcTemplate = NamedParameterJdbcTemplateFactory.createNamedParameterJdbcTemplate(dataSource);
            final UserWriteRepository userWriteRepository = new UserWriteRepository(namedJdbcTemplate);
            final RoleReadRepository roleReadRepository = new RoleReadRepository(namedJdbcTemplate);
            final UserReadRepository userReadRepository = new UserReadRepository(namedJdbcTemplate, roleReadRepository);
            final var alleRoller = StreamSupport.stream(roleReadRepository.findAll().spliterator(), false).toList();
            final var brukerId = userWriteRepository.save("Testbruker", alleRoller.stream().map(Role::id).toList());
            assertThat(userReadRepository.findById(brukerId))
                    .isNotNull()
                    .satisfies(bruker -> {
                        assertThat(bruker.roles()).containsAll(alleRoller);
                    });
            userWriteRepository.delete(brukerId);
        }
    }

    protected DataSource createDataSource() {
        return DataSourceFactory.createDataSource(
                new DataSourceConfig(
                        postgreSQLContainer.getJdbcUrl(),
                        postgreSQLContainer.getUsername(),
                        postgreSQLContainer.getPassword()));
    }
}
