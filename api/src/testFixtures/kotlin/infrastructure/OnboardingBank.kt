package lmirabal.infrastructure

import lmirabal.finance.Amount
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Pot
import lmirabal.model.PotName

interface OnboardingBank : Bank {
    fun createAccount(request: CreateAccountRequest): Account
    fun createPot(request: CreatePotRequest): Pot
}

data class CreateAccountRequest(val address: AccountAddress, val balance: Amount)
data class CreatePotRequest(val account: Account, val name: PotName, val balance: Amount)