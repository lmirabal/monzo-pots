package lmirabal

import lmirabal.finance.Amount
import lmirabal.finance.pounds
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Pot
import lmirabal.model.PotName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class StubBankTest {
    @Test
    fun `creates an account`() {
        val account = bank.createAccount(
            CreateAccountRequest(
                AccountAddress(sortCode = "123456", number = "123456789"),
                100.pounds
            )
        )

        assertEquals(
            Account(
                "id",
                AccountAddress(sortCode = "123456", number = "123456789"),
                100.pounds
            ),
            account
        )
    }

    @Test
    fun `gets an account`() {
        val account = bank.createSampleAccount()

        assertEquals(account, bank.getSampleAccount())
    }

    @Test
    fun `creates a pot for an account`() {
        bank.createSampleAccount()

        val pot = bank.createPot("pot", 100.pounds)

        assertEquals(Pot("id", PotName("pot"), 100.pounds), pot)
    }

    @Test
    fun `gets pots for an account`() {
        val account = bank.createSampleAccount()
        val pot1 = bank.createPot("pot1", 100.pounds)
        val pot2 = bank.createPot("pot2", 200.pounds)

        assertEquals(listOf(pot1, pot2), bank.getPotsFor(account))
    }

    @Test
    fun `moves funds between pots`() {
        val account = bank.createSampleAccount()
        val source = bank.createPot("source", 100.pounds)
        val destination = bank.createPot("destination", 200.pounds)

        val updatedSource = bank.withdraw(source, account, 70.pounds)
        assertEquals(30.pounds, updatedSource.balance)
        assertEquals(70.pounds, bank.getSampleAccountBalance())

        val updatedDestination = bank.deposit(account, destination, 70.pounds)
        assertEquals(270.pounds, updatedDestination.balance)
        assertEquals(0.pounds, bank.getSampleAccountBalance())
    }

    @Test
    fun `fails to withdraw from account more than available funds`() {
        val account = bank.createSampleAccount()
        val pot = bank.createPot("pot", 0.pounds)

        assertThrows<IllegalArgumentException> {
            bank.deposit(from = account, to = pot, 100.pounds)
        }
    }

    @Test
    fun `fails to withdraw from pot more than available funds`() {
        val account = bank.createSampleAccount()
        val pot = bank.createPot("pot", 0.pounds)

        assertThrows<IllegalArgumentException> {
            bank.withdraw(from = pot, to = account, 100.pounds)
        }
    }

    private fun StubBank.getSampleAccountBalance() = getSampleAccount().balance

    private val bank = StubBank { "id" }
    private val sampleAddress = AccountAddress(sortCode = "123456", number = "123456789")
    private val sampleBalance = Amount.ZERO
    private val sampleAccountRequest = CreateAccountRequest(sampleAddress, sampleBalance)
    private val sampleAccount = Account("id", sampleAddress, sampleBalance)

    private fun StubBank.createSampleAccount(): Account {
        return createAccount(sampleAccountRequest)
    }

    private fun StubBank.getSampleAccount(): Account {
        return getAccountBy(sampleAddress)
    }

    private fun StubBank.createPot(name: String, balance: Amount): Pot {
        return createPot(CreatePotRequest(sampleAccount, PotName(name), balance))
    }
}