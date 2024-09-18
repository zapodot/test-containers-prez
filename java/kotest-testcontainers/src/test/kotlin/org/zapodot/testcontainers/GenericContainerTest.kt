package org.zapodot.testcontainers

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.perTest
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

class GenericContainerTest : StringSpec({
    val genericContainer = GenericContainer("testcontainers/helloworld:1.1.0")
        .withExposedPorts(8080)
        .waitingFor(Wait.forHttp("/"))
    listeners(genericContainer.perTest())

    "Ping skal gi PONG" {
        val httpClient = HttpClient(CIO)
        httpClient.get("http://${genericContainer.host}:${genericContainer.getMappedPort(8080)}/ping")
            .asClue { response ->
                response.status.value shouldBe 200
                response.readBytes().toString(Charsets.UTF_8) shouldBe "PONG"
            }
    }
})