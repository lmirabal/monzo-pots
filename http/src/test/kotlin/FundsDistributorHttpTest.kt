package lmirabal

import lmirabal.infrastructure.Account
import lmirabal.infrastructure.Deposit
import lmirabal.infrastructure.DistributeBody
import lmirabal.infrastructure.Remainder
import lmirabal.infrastructure.httpServer
import lmirabal.infrastructure.manifestLens
import lmirabal.model.DistributionManifest
import org.http4k.client.OkHttp
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.with
import org.junit.jupiter.api.AfterEach

class FundsDistributorHttpTest : FundsDistributorContract() {
    private val server = httpServer(FundsDistributorApplication(bank), 0).start()
    override val fundsDistributor: FundsDistributor = FundsDistributorHttpClient(server.port())

    @AfterEach
    fun teardown() {
        server.stop()
    }

    class FundsDistributorHttpClient(private val port: Int) : FundsDistributor {
        private val client = OkHttp()
        override fun distribute(manifest: DistributionManifest) {
            val response = client(
                Request(POST, "http://localhost:$port/distribute")
                    .with(manifestLens of manifest.toBody())
            )
            if (response.status == BAD_REQUEST) throw IllegalArgumentException(response.bodyString())
            if (!response.status.successful)
                throw Exception("Unexpected error: $response")
        }
    }
}

private fun DistributionManifest.toBody(): DistributeBody {
    return DistributeBody(
        Account(mainAccount.sortCode, mainAccount.number),
        source.toString(),
        deposits.map { Deposit(it.to.toString(), it.amount.pence) },
        keepInMainAccount.pence,
        Remainder(remainder.to.toString(), remainder.atLeast.pence)
    )
}