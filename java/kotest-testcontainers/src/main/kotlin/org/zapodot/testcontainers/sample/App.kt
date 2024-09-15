package org.zapodot.testcontainers.sample

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.postgresql.Driver
import javax.sql.DataSource

private val LOGGER = mu.KotlinLogging.logger {  }
object App {
    val appConfiguration by lazy {
        ConfigLoaderBuilder.default()
            .addDefaultPropertySources()
            .build()
            .loadConfig<AppConfiguration>()
            .fold({
                throw IllegalStateException("Failed to load configuration: " + it.description())
            }) {
                it
            }
    }

    val dataSource: DataSource by lazy {
        HikariDataSource(hikariConfig)
    }

    private val hikariConfig: HikariConfig by lazy {
        HikariConfig().apply {
            jdbcUrl = appConfiguration.jdbcUrl
            username = appConfiguration.username
            password = appConfiguration.password
            driverClassName = Driver::class.java.name
            maximumPoolSize = 5
        }
    }


}

data class AppConfiguration(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val amqpHost: String?
)

fun main(args: Array<String>) {
    Database.connect(App.dataSource)
    LOGGER.info { "Connected to database" }
}