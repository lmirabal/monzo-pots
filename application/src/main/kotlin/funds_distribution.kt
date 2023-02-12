package lmirabal

import lmirabal.finance.Amount
import lmirabal.infrastructure.Bank
import lmirabal.model.Account
import lmirabal.model.DistributionManifest
import lmirabal.model.Pot
import lmirabal.model.PotName

fun distributeFunds(bank: Bank, manifest: DistributionManifest) {
    val currentAccount: Account = bank.getAccountBy(manifest.mainAccount)
    val pots: List<Pot> = bank.getPotsFor(currentAccount)

    val missingPots = manifest.potNames.missingFrom(pots)
    if (missingPots.isNotEmpty())
        throw IllegalArgumentException("Manifest contains invalid pots=$missingPots")

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

internal operator fun List<Pot>.get(potName: PotName): Pot = first { it.name == potName }

private fun List<PotName>.missingFrom(pots: List<Pot>): List<PotName> {
    val existingPotNames = pots.map { it.name }
    return filter { potName -> potName !in existingPotNames }
}

private val DistributionManifest.depositAmount
    get() = deposits
        .map { it.amount }
        .reduce { amount1, amount2 -> amount1 + amount2 }

internal val DistributionManifest.minimumSourceFunds
    get() = keepInMainAccount + depositAmount + remainder.atLeast
internal val DistributionManifest.potNames
    get() = listOf(source) + deposits.map { it.to } + remainder.to

internal fun DistributionManifest.remainderAmount(sourceBalance: Amount): Amount =
    sourceBalance - keepInMainAccount - depositAmount

