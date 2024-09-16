package org.zapodot.testcontainers.sample.publish

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.zapodot.testcontainers.ContainerTestSetup.connectionFactory
import org.zapodot.testcontainers.sample.User
import kotlin.time.Duration.Companion.minutes

class UserPublisherTest : StringSpec({

    "Kan publisere en bruker" {

        val exchangeName = "test"
        val destination = "test"
        val publisher = autoClose(
            UserPublisher(
                connectionFactory = connectionFactory, topicName = exchangeName
            )
        )
        val recieveConnection = autoClose(connectionFactory.newConnection())
        val recieveChannel = autoClose(recieveConnection.createChannel())

        recieveChannel.queueDeclare(destination, false, false, false, null)
        recieveChannel.queueBind(destination, exchangeName, "")
        val newUsers = mutableListOf<User>()
        recieveChannel.basicConsume(destination, false, object : DefaultConsumer(recieveChannel) {

            override fun handleDelivery(
                consumerTag: String,
                envelope: Envelope,
                properties: AMQP.BasicProperties?,
                body: ByteArray?
            ) {
                if(body != null) {
                    newUsers.add(jacksonObjectMapper().readValue(body))
                }
                recieveChannel.basicAck(envelope.deliveryTag, false)
            }
        })
        val user = User(1, "Ola", emptyList())
        publisher.publish(user)
        eventually(1.minutes) {
            newUsers shouldHaveSize 1
            newUsers shouldContain user
        }

    }

})
