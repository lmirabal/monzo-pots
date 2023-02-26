package lmirabal.infrastructure

import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HttpPingTest {
    private val client = OkHttp()
    private val server = httpServer(0)

    @BeforeEach
    fun setup() {
        server.start()
    }

    @AfterEach
    fun teardown() {
        server.stop()
    }

    @Test
    fun `responds to ping`() {
        assertThat(
            client(Request(GET, "http://localhost:${server.port()}/ping")),
            hasStatus(OK)
        )
    }

    @Test
    fun `doesn't respond to ping if wrong url`() {
        assertThat(
            client(Request(GET, "http://localhost:${server.port()}/hello")),
            hasStatus(NOT_FOUND)
        )
    }
}