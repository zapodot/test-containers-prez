package org.zapodot.testcontainers.sample.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table

object Users: LongIdTable(columnName = "id", name = "users") {
    val name = varchar("name", 255)
}

object Roles: LongIdTable(columnName = "id", name = "roles") {
    val name = varchar("name", 255)
}

object UserRoles: Table(name = "user_roles") {
    val user = reference(name = "user_id", Users.id)
    val role = reference(name = "role_id", Roles.id)
    override val primaryKey: PrimaryKey = PrimaryKey(user, role)
}

