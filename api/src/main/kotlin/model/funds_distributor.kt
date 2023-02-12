package lmirabal.model

import lmirabal.finance.Amount

data class Deposit(val to: PotName, val amount: Amount)
data class Remainder(val to: PotName, val atLeast: Amount)
data class DistributionManifest(
    val mainAccount: AccountAddress,
    val source: PotName,
    val deposits: List<Deposit>,
    val keepInMainAccount: Amount,
    val remainder: Remainder
)