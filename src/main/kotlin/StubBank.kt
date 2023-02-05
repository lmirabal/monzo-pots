package lmirabal

import lmirabal.finance.Amount
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Pot
import lmirabal.model.PotName
import java.util.*

data class CreateAccountRequest(val address: AccountAddress, val balance: Amount)
data class CreatePotRequest(val account: Account, val name: PotName, val balance: Amount)

class StubBank(private val idGenerator: IdGenerator = randomUuid()) : Bank {
    private val accounts = mutableMapOf<AccountAddress, Account>()
    private val pots = mutableMapOf<Account, Set<Pot>>()

    fun createAccount(request: CreateAccountRequest): Account =
        Account(id = idGenerator(), request.address, request.balance)
            .also { account -> accounts[request.address] = account }

    fun createPot(request: CreatePotRequest): Pot {
        return Pot(idGenerator(), request.name, request.balance)
            .also { pot ->
                pots.merge(request.account, setOf(pot)) { existing, newPot -> existing + newPot }
            }
    }

    override fun getAccountBy(address: AccountAddress): Account = accounts.getValue(address)

    override fun getPotsFor(account: Account): List<Pot> = pots.getValue(account).toList()

    override fun deposit(from: Account, to: Pot, amount: Amount): Pot {
        val account: Account = accounts.getValue(from.address)
        accounts[from.address] = account.withdraw(amount)
        val allPots = pots.getValue(from)
        val pot = allPots.first { it == to }
        val updatedPot = pot.deposit(amount)
        pots[from] = allPots - pot + updatedPot
        return updatedPot
    }

    override fun withdraw(from: Pot, to: Account, amount: Amount): Pot {
        val account: Account = accounts.getValue(to.address)
        accounts[to.address] = account.deposit(amount)
        val allPots = pots.getValue(to)
        val pot = allPots.first { it == from }
        val updatedPot = pot.withdraw(amount)
        pots[to] = allPots - pot + updatedPot
        return updatedPot
    }
}

fun Account.deposit(amount: Amount): Account = Account(id, address, balance + amount)
fun Account.withdraw(amount: Amount): Account {
    if (balance < amount) throw IllegalArgumentException("Not enough funds! balance=$balance withdrawal=$amount")
    return Account(id, address, balance - amount)
}

fun Pot.deposit(amount: Amount): Pot = Pot(id, name, balance + amount)
fun Pot.withdraw(amount: Amount): Pot {
    if (balance < amount) throw IllegalArgumentException("Not enough funds! balance=$balance withdrawal=$amount")
    return Pot(id, name, balance - amount)
}

typealias IdGenerator = () -> String

private fun randomUuid(): IdGenerator = { UUID.randomUUID().toString() }
