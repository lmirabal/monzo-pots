package lmirabal.model

import lmirabal.finance.Amount

data class AccountAddress(val sortCode: String, val number: String)
data class Account(val id: String, val address: AccountAddress, val balance: Amount)
@JvmInline
value class PotName(private val value: String) {
    override fun toString() = value
}
data class Pot(val id: String, val name: PotName, val balance: Amount)