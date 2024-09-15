package org.zapodot.testcontainers.sample.update;

import org.zapodot.testcontainers.sample.csv.UserWithRoles;
import org.zapodot.testcontainers.sample.model.Role;
import org.zapodot.testcontainers.sample.model.User;
import org.zapodot.testcontainers.sample.repositories.read.RoleReadRepository;
import org.zapodot.testcontainers.sample.repositories.write.UserWriteRepository;

import java.util.List;

public class UserUpdater {
    private final RoleReadRepository roleReadRepository;
    private final UserWriteRepository userWriteRepository;

    public UserUpdater(RoleReadRepository roleReadRepository, UserWriteRepository userWriteRepository) {
        this.roleReadRepository = roleReadRepository;
        this.userWriteRepository = userWriteRepository;
    }

    public long insertUsers(final List<UserWithRoles> usersWithRoles) {
        return usersWithRoles
                .stream()
                .map(userWithRoles -> userWriteRepository.save(userWithRoles.name(), userWithRoles.roles().stream().map(
                        roleReadRepository::findByName).map(Role::id).toList()))
                .count();
    }
}
