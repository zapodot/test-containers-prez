package org.zapodot.testcontainers.sample.publish

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rabbitmq.client.ConnectionFactory
import org.zapodot.testcontainers.sample.User

private val logger = mu.KotlinLogging.logger {}
class UserPublisher(connectionFactory: ConnectionFactory, private val destination: String): AutoCloseable {
    private val connection = connectionFactory.newConnection()
    private val channel = connection.createChannel()
    private val mapper = jacksonObjectMapper()
        .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun publish(user: User) {
        logger.info { "Publishing user $user to $destination" }
        channel.basicPublish(destination, "", null, mapper.writeValueAsBytes(user))
    }

    override fun close() {
        channel.close()
        connection.close()
    }
}