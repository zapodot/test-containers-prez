package org.zapodot.testcontainers.sample.db

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.zapodot.testcontainers.sample.Role
import org.zapodot.testcontainers.sample.User

object UserReadRepository {
    fun findById(id: Long): User? {
        return Users.selectAll().where { Users.id eq id }.map {
            User(
                id = it[Users.id].value,
                name = it[Users.name],
                roles = RoleReadRepository.findByUserId(it[Users.id].value)
            )
        }.singleOrNull()
    }
}

object RoleReadRepository {
    fun findById(id: Long): Role? {
        return Roles.selectAll().where { Roles.id eq id }.map {
            Role(
                id = it[Roles.id].value,
                name = it[Roles.name]
            )
        }.singleOrNull()
    }

    fun findByName(name: String): Role? {
        return Roles.selectAll().where { Roles.name eq name }.map {
            Role(
                id = it[Roles.id].value,
                name = it[Roles.name]
            )
        }.singleOrNull()
    }

    fun findByUserId(userId: Long): List<Role> {
        return Roles.join(UserRoles, JoinType.INNER)
            .select(Roles.id, Roles.name)
            .where { UserRoles.user eq userId }
            .map {
                Role(
                    id = it[Roles.id].value,
                    name = it[Roles.name]
                )
            }
    }

    fun findAll(): List<Role> {
        return Roles.selectAll().map {
            Role(
                id = it[Roles.id].value,
                name = it[Roles.name]
            )
        }
    }
}