package org.zapodot.testcontainers.sample.repositories.write;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.zapodot.testcontainers.sample.repositories.BrukerRolleTabell;
import org.zapodot.testcontainers.sample.repositories.BrukerTabell;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserWriteRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserWriteRepository.class);
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public UserWriteRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = Objects.requireNonNull(namedParameterJdbcOperations);
    }

    public Long save(final String name, final List<Long> roleIds) {
        LOGGER.info("Lagrer bruker med navn {} og roller {}", name, roleIds);
        final var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcOperations.update("INSERT INTO users (name) VALUES (:" + BrukerTabell.NAVN + ")",
                new MapSqlParameterSource(Map.of(BrukerTabell.NAVN, name)), keyHolder);
        final long userId = ((Number) Objects.requireNonNull(keyHolder.getKeys()).get(BrukerTabell.ID)).longValue();
        if(! roleIds.isEmpty()) {
            Map[] batchValues = roleIds.stream()
                    .distinct()
                    .filter(Objects::nonNull)
                    .map(roleId -> Map.of(BrukerRolleTabell.BRUKER_ID, userId, BrukerRolleTabell.ROLLE_ID, roleId))
                    .toArray(Map[]::new);
            if(batchValues.length != roleIds.size()) {
                throw new IllegalArgumentException("En eller flere roller er ikke gyldige. Fant " + batchValues.length + " roller, men forventet " + roleIds.size());
            }
            namedParameterJdbcOperations.batchUpdate("INSERT INTO user_roles (user_id, role_id) VALUES (:" + BrukerRolleTabell.BRUKER_ID + ", :" + BrukerRolleTabell.ROLLE_ID + ")",
                    batchValues);
        }
        return userId;
    }

    public void delete(final long id) {
        namedParameterJdbcOperations.update("DELETE FROM user_roles WHERE user_id = :userId",
                Map.of("userId", id));
        namedParameterJdbcOperations.update("DELETE FROM users WHERE id = :id",
                Map.of("id", id));
    }
}
