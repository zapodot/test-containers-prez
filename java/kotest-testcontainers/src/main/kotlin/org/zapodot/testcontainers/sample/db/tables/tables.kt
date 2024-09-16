package org.zapodot.testcontainers.sample.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table

object Users: LongIdTable(columnName = "id", name = "users") {
    val name = varchar("name", 255).uniqueIndex()
}

object Roles: LongIdTable(columnName = "id", name = "roles") {
    val name = varchar("name", 255).uniqueIndex()
}

object UserRoles: Table(name = "user_roles") {
    val userRef = reference(name = "user_id", Users.id)
    val roleRef = reference(name = "role_id", Roles.id)
    override val primaryKey: PrimaryKey = PrimaryKey(userRef, roleRef)
}

