package lmirabal.infrastructure

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import lmirabal.finance.Amount
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Pot
import lmirabal.model.PotName
import org.http4k.client.JavaHttpClient
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Header
import java.util.*

class MonzoApi(accessToken: String) : Bank {
    private val client: HttpHandler = ClientFilters.SetBaseUriFrom(Uri.of("https://api.monzo.com"))
        .then(ClientFilters.BearerAuth(accessToken))
        .then(DebuggingFilters.PrintRequestAndResponse())
        .then(JavaHttpClient())

    override fun getAccountBy(address: AccountAddress): Account {
        fun getAccounts(): List<AccountResponse> {
            val accountsRequest = Request(Method.GET, "/accounts")
                .query("account_type", "uk_retail")
            return accountsLens(client(accountsRequest))
                .filter { !it.closed }
        }

        fun getBalance(accountId: String): Amount {
            val balanceRequest = Request(Method.GET, "/balance")
                .query("account_id", accountId)
            return balanceLens(client(balanceRequest))
        }

        return getAccounts()
            .first { account -> AccountAddress(account.sort_code, account.account_number) == address }
            .let { it.asAccountWithBalance(getBalance(it.id)) }
    }

    override fun getPotsFor(account: Account): List<Pot> {
        val potsRequest = Request(Method.GET, "/pots").query("current_account_id", account.id)
        return potsLens(client(potsRequest))
            .filter { !it.deleted }
            .map { it.asPot() }
    }

    override fun deposit(from: Account, to: Pot, amount: Amount): Pot {
        val depositRequest = Request(Method.PUT, "/pots/${to.id}/deposit")
            .with(Header.CONTENT_TYPE of ContentType.APPLICATION_FORM_URLENCODED)
            .form("source_account_id", from.id)
            .form("amount", amount.pence.toString())
            .form("dedupe_id", UUID.randomUUID().toString())
        return potLens(client(depositRequest))
    }

    override fun withdraw(from: Pot, to: Account, amount: Amount): Pot {
        val withdrawRequest = Request(Method.PUT, "/pots/${from.id}/withdraw")
            .with(Header.CONTENT_TYPE of ContentType.APPLICATION_FORM_URLENCODED)
            .form("destination_account_id", to.id)
            .form("amount", amount.pence.toString())
            .form("dedupe_id", UUID.randomUUID().toString())
        return potLens(client(withdrawRequest))
    }
}

private val accountsLens = Body.auto<Accounts>()
    .map { response -> response.accounts }
    .toLens()

private val balanceLens = Body.auto<Balance>()
    .map { response -> response.balance }
    .map { pence -> Amount(pence) }
    .toLens()

private val potsLens = Body.auto<Pots>()
    .map { response -> response.pots }
    .toLens()

private val potLens = Body.auto<PotResponse>().map(PotResponse::asPot).toLens()

private fun AccountResponse.asAccountWithBalance(balance: Amount) =
    Account(id, AccountAddress(sort_code, account_number), balance)

private fun PotResponse.asPot() = Pot(id, PotName(name.trim()), Amount(pence = balance))

@Serializable
private class Accounts(val accounts: List<AccountResponse>)

@Serializable
private class Balance(val balance: Long)

@Suppress("unused")
@Serializable
private class AccountResponse(
    val id: String,
    val closed: Boolean,
    val created: Instant,
    val description: String,
    val account_number: String,
    val sort_code: String
)

@Serializable
private class Pots(val pots: List<PotResponse>)

@Suppress("unused")
@Serializable
private class PotResponse(
    val id: String,
    val name: String,
    val balance: Long,
    val updated: Instant,
    val deleted: Boolean
)