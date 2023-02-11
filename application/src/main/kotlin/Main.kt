package lmirabal

import lmirabal.finance.pound
import lmirabal.infrastructure.MonzoApi
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Pot

fun main(args: Array<String>) {
    val monzo = MonzoApi(accessToken = args[0])
    val currentAccount: Account = monzo.getAccountBy(AccountAddress(args[1], args[2]))
    println(currentAccount)

    val pots: List<Pot> = monzo.getPotsFor(currentAccount)
    println(pots)

    val pot = pots[0]
    val afterDeposit = monzo.deposit(currentAccount, pot, 1.pound)
    println(afterDeposit)

    val afterWithdrawal = monzo.withdraw(pot, currentAccount, 1.pound)
    println(afterWithdrawal)
}