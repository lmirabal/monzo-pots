package lmirabal.infrastructure

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.filter.ServerFilters.CatchAll
import org.http4k.filter.ServerFilters.CatchLensFailure
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun httpServer(port: Int): Http4kServer = httpApp().asServer(Jetty(port))

private fun httpApp(): HttpHandler =
    CatchAll()
        .then(CatchLensFailure())
        .then(
            routes(
                "/ping" bind GET to { _: Request -> Response(Status.OK) }
            )
        )