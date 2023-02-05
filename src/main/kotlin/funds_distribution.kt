package lmirabal

import lmirabal.finance.Amount
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Pot
import lmirabal.model.PotName

fun distributeFunds(bank: Bank, manifest: DistributionManifest) {
    val currentAccount: Account = bank.getAccountBy(manifest.mainAccount)
    println(currentAccount)
    val pots = bank.getPotsFor(currentAccount)

    val sourcePot = pots[manifest.source]
    if (sourcePot.balance < manifest.minimumSourceFunds)
        throw IllegalArgumentException(
            "Not enough funds: required=${manifest.minimumSourceFunds} available=${sourcePot.balance}"
        )
    bank.withdraw(from = sourcePot, to = currentAccount, sourcePot.balance)

    for (deposit in manifest.deposits) {
        val targetPot = pots[deposit.to]
        bank.deposit(from = currentAccount, to = targetPot, amount = deposit.amount)
    }

    val remainderBalance = manifest.remainderAmount(sourcePot.balance)

    val remainderPot = pots[manifest.remainder.to]
    bank.deposit(from = currentAccount, to = remainderPot, amount = remainderBalance)
}

operator fun List<Pot>.get(potName: PotName): Pot = first { it.name == potName }

data class Deposit(val to: PotName, val amount: Amount)
data class Remainder(val to: PotName, val atLeast: Amount)
data class DistributionManifest(
    val mainAccount: AccountAddress,
    val source: PotName,
    val deposits: List<Deposit>,
    val keepInMainAccount: Amount,
    val remainder: Remainder
) {
    private val depositAmount = deposits
        .map { it.amount }
        .reduce { amount1, amount2 -> amount1 + amount2 }

    val minimumSourceFunds = keepInMainAccount + depositAmount + remainder.atLeast
    fun remainderAmount(sourceBalance: Amount): Amount = sourceBalance - keepInMainAccount - depositAmount
}
