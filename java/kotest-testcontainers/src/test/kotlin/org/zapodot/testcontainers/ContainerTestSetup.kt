package org.zapodot.testcontainers

import com.rabbitmq.client.ConnectionFactory
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.ProjectListener
import io.kotest.extensions.testcontainers.perProject
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.zapodot.testcontainers.sample.db.tables.Roles
import org.zapodot.testcontainers.sample.db.tables.UserRoles
import org.zapodot.testcontainers.sample.db.tables.Users
import kotlin.time.Duration.Companion.minutes

private val log = mu.KotlinLogging.logger {  }

object ContainerTestSetup : AbstractProjectConfig() {
    val postgreSQLContainer = PostgreSQLContainer("postgres:12.20-alpine")
    val database: Database
        get() =
            if (postgreSQLContainer.isRunning) {
                Database.connect(
                    url = postgreSQLContainer.jdbcUrl,
                    driver = postgreSQLContainer.driverClassName,
                    user = postgreSQLContainer.username,
                    password = postgreSQLContainer.password
                )
            } else {
                error("PostgreSQL container is not running yet")
            }

    val rabbitMQContainer = RabbitMQContainer("rabbitmq:4.0.0-rc.1-alpine")
        .withLogConsumer { log.debug { it.utf8StringWithoutLineEnding } }

    val connectionFactory get() = ConnectionFactory().apply {
        host = rabbitMQContainer.host
        port = rabbitMQContainer.amqpPort
        username = rabbitMQContainer.adminUsername
        password = rabbitMQContainer.adminPassword
    }


    override fun extensions(): List<Extension> = listOf(
        postgreSQLContainer.perProject(),
        ExposeDDLListener(postgreSQLContainer),
        rabbitMQContainer.perProject()
    )

}

class ExposeDDLListener(private val jdbcDatabaseContainer: JdbcDatabaseContainer<*>) : ProjectListener {
    override suspend fun beforeProject() {
        val database = Database.connect(
            url = jdbcDatabaseContainer.jdbcUrl,
            driver = jdbcDatabaseContainer.driverClassName,
            user = jdbcDatabaseContainer.username,
            password = jdbcDatabaseContainer.password
        )
        eventually(3.minutes) {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(Users, Roles, UserRoles)
            }
        }

    }
}