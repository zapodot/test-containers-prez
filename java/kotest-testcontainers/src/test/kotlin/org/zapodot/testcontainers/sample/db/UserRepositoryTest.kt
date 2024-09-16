package org.zapodot.testcontainers.sample.db

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryTest : StringSpec({

    "Skal ikke finne noe" {
        transaction {
            addLogger(StdOutSqlLogger)
            UserReadRepository.findById(Long.MAX_VALUE) shouldBe null
        }
    }

    "Legger inn en bruker, finner den igjen og så sletter den" {
        transaction {
            addLogger(StdOutSqlLogger)
            val user = UserWriteRepository.create("Ola")
            UserReadRepository.findById(user.id) shouldBe user
            UserWriteRepository.delete(user.id)
            UserReadRepository.findById(user.id) shouldBe null
        }
    }

    "Skal kunne opprette en bruker med flere roller" {
        transaction {
            addLogger(StdOutSqlLogger)
            val role1 = RoleWriteRepository.create("Role1")
            val role2 = RoleWriteRepository.create("Role2")
            val user = UserWriteRepository.create("Ola", listOf(role1, role2))

            UserReadRepository.findById(user.id) shouldBe user
            UserWriteRepository.delete(user.id)
            RoleWriteRepository.delete(role1.id)
            RoleWriteRepository.delete(role2.id)
        }
    }

    "Skal kunne opprette og oppdatere en bruker med flere roller" {
        transaction {
            addLogger(StdOutSqlLogger)
            val role1 = RoleWriteRepository.create("Role1")
            val role2 = RoleWriteRepository.create("Role2")
            val user = UserWriteRepository.create("Ola", listOf(role1))
            UserReadRepository.findById(user.id) shouldBe user

            UserWriteRepository.update(user.copy(roles = listOf(role2)))
            UserReadRepository.findById(user.id) shouldBe user.copy(roles = listOf(role2))

            UserWriteRepository.delete(user.id)
            RoleWriteRepository.delete(role1.id)
            RoleWriteRepository.delete(role2.id)
        }
    }
})
