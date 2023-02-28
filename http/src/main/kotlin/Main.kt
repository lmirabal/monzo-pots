package lmirabal

import lmirabal.infrastructure.httpServer

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 80
    val server = httpServer(port).start()
    println("Server started on ${server.port()}")
}