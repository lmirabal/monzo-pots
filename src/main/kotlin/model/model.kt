package lmirabal.model

import lmirabal.finance.Amount

data class AccountAddress(val sortCode: String, val number: String)
data class Account(val id: String, val address: AccountAddress, val balance: Amount)
data class Pot(val id: String, val name: String, val balance: Amount)