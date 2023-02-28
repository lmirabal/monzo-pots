package lmirabal.infrastructure

import kotlinx.serialization.Serializable
import lmirabal.FundsDistributor
import lmirabal.finance.Amount
import lmirabal.model.AccountAddress
import lmirabal.model.DistributionManifest
import lmirabal.model.PotName
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters.CatchAll
import org.http4k.filter.ServerFilters.CatchLensFailure
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun httpServer(application: FundsDistributor, port: Int): Http4kServer = httpApp(application).asServer(Jetty(port))

private fun httpApp(application: FundsDistributor): HttpHandler =
    CatchAll()
        .then(CatchLensFailure())
        .then(
            routes(
                "/ping" bind GET to { _: Request -> Response(OK) },
                "/distribute" bind POST to { request ->
                    val body = manifestLens(request)
                    try {
                        application.distribute(body.toManifest())
                        Response(OK)
                    } catch (e: IllegalArgumentException) {
                        Response(BAD_REQUEST).body(e.message.orEmpty())
                    }
                }
            )
        )

private fun DistributeBody.toManifest(): DistributionManifest {
    return DistributionManifest(
        AccountAddress(mainAccount.sortCode, mainAccount.number),
        PotName(sourcePot),
        deposits.map { lmirabal.model.Deposit(PotName(it.pot), Amount(it.amount)) },
        Amount(keepInMainAccount),
        lmirabal.model.Remainder(PotName(remainder.pot), Amount(remainder.minimumAmount))
    )
}

var manifestLens = Body.auto<DistributeBody>().toLens()

@Serializable
class DistributeBody(
    val mainAccount: Account,
    val sourcePot: String,
    val deposits: List<Deposit>,
    val keepInMainAccount: Long,
    val remainder: Remainder
)

@Serializable
class Account(val sortCode: String, val number: String)

@Serializable
class Deposit(val pot: String, val amount: Long)

@Serializable
class Remainder(val pot: String, val minimumAmount: Long)
