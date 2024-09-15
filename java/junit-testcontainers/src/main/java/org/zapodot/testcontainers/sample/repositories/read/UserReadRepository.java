package org.zapodot.testcontainers.sample.repositories.read;

import jakarta.annotation.Nullable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.zapodot.testcontainers.sample.model.User;
import org.zapodot.testcontainers.sample.repositories.BrukerTabell;

import java.util.Map;

public class UserReadRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RoleReadRepository roleReadRepository;

    public UserReadRepository(NamedParameterJdbcOperations namedParameterJdbcOperations, RoleReadRepository roleReadRepository) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.roleReadRepository = roleReadRepository;
    }

    public @Nullable User findById(final long id) {
        return namedParameterJdbcOperations.queryForObject("SELECT * FROM users WHERE id = :" + BrukerTabell.ID,
                Map.of(BrukerTabell.ID, id),
                (rs, rowNum) -> new User(rs.getLong(BrukerTabell.ID),
                        rs.getString(BrukerTabell.NAVN),
                        roleReadRepository.findByUserId(rs.getLong(BrukerTabell.ID))));
    }
}
