package org.zapodot.testcontainers.sample

data class User(val id: Long, val name: String, val roles: List<Role>)
data class Role(val id: Long, val name: String)