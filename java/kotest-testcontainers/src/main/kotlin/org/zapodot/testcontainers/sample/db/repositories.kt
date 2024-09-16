package org.zapodot.testcontainers.sample.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.zapodot.testcontainers.sample.Role
import org.zapodot.testcontainers.sample.User
import org.zapodot.testcontainers.sample.db.tables.Roles
import org.zapodot.testcontainers.sample.db.tables.UserRoles
import org.zapodot.testcontainers.sample.db.tables.Users

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
            .where { UserRoles.userRef eq userId }
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

object RoleWriteRepository {
    fun create(name: String): Role {
        val id = Roles.insertAndGetId {
            it[Roles.name] = name
        }
        return Role(id.value, name)
    }

    fun delete(id: Long) {
        Roles.deleteWhere { Roles.id eq id }
    }
}

object UserWriteRepository {
    fun create(name: String, roles: List<Role> = emptyList()): User {
        val id = Users.insertAndGetId {
            it[Users.name] = name
        }
        if(roles.isNotEmpty()) {
            roles.forEach { currentRole ->
                UserRoles.insert {
                    it[userRef] = id
                    it[roleRef] = currentRole.id
                }
            }
        }
        return User(id.value, name, roles)
    }

    fun update(user: User) {
        Users.update({ Users.id eq user.id }) {
            it[name] = user.name
        }
        val rolesBeforeUpdate = RoleReadRepository.findByUserId(user.id)
        rolesBeforeUpdate.forEach { currentRole ->
            if(!user.roles.contains(currentRole)) {
                UserRoles.deleteWhere { UserRoles.userRef eq user.id and (UserRoles.roleRef eq currentRole.id) }
            }
        }
        user.roles.forEach { currentRole ->
            if(!rolesBeforeUpdate.contains(currentRole)) {
                UserRoles.insert { it[userRef] = user.id; it[roleRef] = currentRole.id }
            }
        }
    }

    fun delete(id: Long) {
        UserRoles.deleteWhere { UserRoles.userRef eq id }
        Users.deleteWhere { Users.id eq id }
    }
}