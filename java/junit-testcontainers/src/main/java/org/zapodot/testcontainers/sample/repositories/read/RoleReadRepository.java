package org.zapodot.testcontainers.sample.repositories.read;

import jakarta.annotation.Nullable;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.zapodot.testcontainers.sample.model.Role;
import org.zapodot.testcontainers.sample.repositories.BrukerRolleTabell;
import org.zapodot.testcontainers.sample.repositories.RolleTabell;

import java.util.Map;

public class RoleReadRepository {
    private final RowMapper<Role> roleRowMapper = (rs, rowNum) -> new Role(rs.getLong(RolleTabell.ID), rs.getString(RolleTabell.NAVN));
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public RoleReadRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    public @Nullable Role findById(final long id) {
        try {
            return namedParameterJdbcOperations.queryForObject("SELECT * FROM roles WHERE " + RolleTabell.ID + " = :" + RolleTabell.ID,
                    Map.of(RolleTabell.ID, id),
                    roleRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public @Nullable Role findByName(final String name) {
        try {
            return namedParameterJdbcOperations.queryForObject("SELECT * FROM roles WHERE " + RolleTabell.NAVN + " = :" + RolleTabell.NAVN,
                    Map.of(RolleTabell.NAVN, name),
                    roleRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public Iterable<Role> findAll() {
        return namedParameterJdbcOperations.query("SELECT * FROM roles",
                roleRowMapper);
    }

    public Iterable<Role> findByUserId(final long userId) {
        return namedParameterJdbcOperations.query("SELECT r.*\n" +
                        "FROM user_roles ur INNER JOIN roles r ON r.id = ur.role_id\n" +
                        "WHERE ur.user_id = :" + BrukerRolleTabell.BRUKER_ID,
                Map.of(BrukerRolleTabell.BRUKER_ID, userId),
                roleRowMapper);
    }
}
