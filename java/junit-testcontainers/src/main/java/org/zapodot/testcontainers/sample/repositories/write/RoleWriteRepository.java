package org.zapodot.testcontainers.sample.repositories.write;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.zapodot.testcontainers.sample.model.Role;
import org.zapodot.testcontainers.sample.repositories.RolleTabell;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoleWriteRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public RoleWriteRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    public Role save(final String roleName) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcOperations.update("INSERT INTO roles (" + RolleTabell.NAVN + ") VALUES (:" + RolleTabell.NAVN + ")",
                new MapSqlParameterSource(Map.of(RolleTabell.NAVN, roleName)), keyHolder);
        return new Role(((Number) Objects.requireNonNull(keyHolder.getKeys()).get(RolleTabell.ID)).longValue(), roleName);
    }

    public void delete(final long id) {
        namedParameterJdbcOperations.update("DELETE FROM roles WHERE id = :id",
                Map.of("id", id));
    }
}
