package org.zapodot.testcontainers.sample.csv;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CsvReaderTest {

    @DisplayName("Les brukere fra CSV-fil")
    @Test
    void readFromFile() throws IOException {
        final var usersWithRoles = CsvReader.readFromFile(new ClassPathResource("users-with-roles.csv").getFile());
        assertNotNull(usersWithRoles);
        assertThat(usersWithRoles).hasSize(2);
        assertThat(usersWithRoles.getFirst()).satisfies(userWithRoles -> {
                    assertThat(userWithRoles.name()).isEqualTo("John");
                    assertThat(userWithRoles.roles()).containsExactlyInAnyOrder("Administrator");
                }
        );
        assertThat(usersWithRoles.get(1)).satisfies(userWithRoles -> {
                    assertThat(userWithRoles.name()).isEqualTo("Anna");
                    assertThat(userWithRoles.roles()).containsExactlyInAnyOrder("Developer", "Role administator");
                }
        );
    }
}