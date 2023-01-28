package lmirabal

import lmirabal.finance.Amount
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Pot

interface Bank {
    fun getAccountBy(address: AccountAddress): Account
    fun getPotsFor(account: Account): List<Pot>
    fun deposit(from: Account, to: Pot, amount: Amount): Pot
    fun withdraw(from: Pot, to: Account, amount: Amount): Pot
}