package org.zapodot.testcontainers.sample.db

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class RoleRepositoryTest: StringSpec({
    "Skal ikke finne noe" {
        transaction {
            addLogger(StdOutSqlLogger)
            RoleReadRepository.findById(Long.MAX_VALUE) should beNull()
        }
    }

    "Oppretter rolle, finner den igjen og sletter den" {
        transaction {
            addLogger(StdOutSqlLogger)
            val role = RoleWriteRepository.create(testCase.name.testName)
            RoleReadRepository.findById(role.id) shouldBe role
            RoleWriteRepository.delete(role.id)
            RoleReadRepository.findById(role.id) should beNull()
        }
    }

    "Skal kunne finne en rolle på navn" {
        transaction {
            addLogger(StdOutSqlLogger)
            val role = RoleWriteRepository.create(testCase.name.testName)
            RoleReadRepository.findByName(role.name) shouldBe role
            RoleWriteRepository.delete(role.id)
        }
    }
})