package lmirabal

import lmirabal.infrastructure.MonzoApi
import lmirabal.infrastructure.httpServer

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 80
    val accessToken = System.getenv("ACCESS_TOKEN") ?: throw Exception("Missing ACCESS_TOKEN env variable")
    val server = httpServer(FundsDistributorApplication(MonzoApi(accessToken)), port).start()
    println("Server started on ${server.port()}")
}