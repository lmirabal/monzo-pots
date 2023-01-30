package lmirabal

import lmirabal.finance.pounds
import lmirabal.model.AccountAddress
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class FundsDistributionTest {

    private val bank = StubBank()
    private val accountAddress = AccountAddress("123456", "123456789")

    @Test
    fun `distribute funds`() {
        val account = bank.createAccount(CreateAccountRequest(accountAddress, 0.pounds))
        val source = PotName("source")
        val destination1 = PotName("destination 1")
        val destination2 = PotName("destination 2")
        val remainder = PotName("remainder")
        bank.createPot(CreatePotRequest(account, source, 300.pounds))
        bank.createPot(CreatePotRequest(account, destination1, 0.pounds))
        bank.createPot(CreatePotRequest(account, destination2, 0.pounds))
        bank.createPot(CreatePotRequest(account, remainder, 0.pounds))
        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = source,
            deposits = listOf(
                Deposit(to = destination1, amount = 50.pounds),
                Deposit(to = destination2, amount = 70.pounds)
            ),
            keepInMainAccount = 30.pounds,
            remainder = Remainder(to = remainder, atLeast = 10.pounds)
        )

        distributeFunds(bank, manifest)

        val pots = bank.getPotsFor(account)
        assertEquals(0.pounds, pots[source].balance)
        assertEquals(30.pounds, bank.getAccountBy(accountAddress).balance)
        assertEquals(50.pounds, pots[destination1].balance)
        assertEquals(70.pounds, pots[destination2].balance)
        assertEquals(150.pounds, pots[remainder].balance)
    }

    @Test
    fun `distribute just enough funds`() {
        val account = bank.createAccount(CreateAccountRequest(accountAddress, 0.pounds))
        val source = PotName("source")
        val destination = PotName("destination")
        val remainder = PotName("remainder")
        bank.createPot(CreatePotRequest(account, source, 90.pounds))
        bank.createPot(CreatePotRequest(account, destination, 0.pounds))
        bank.createPot(CreatePotRequest(account, remainder, 0.pounds))
        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = source,
            deposits = listOf(Deposit(to = destination, amount = 50.pounds)),
            keepInMainAccount = 30.pounds,
            remainder = Remainder(to = remainder, atLeast = 10.pounds)
        )

        distributeFunds(bank, manifest)

        val pots = bank.getPotsFor(account)
        assertEquals(0.pounds, pots[source].balance)
        assertEquals(30.pounds, bank.getAccountBy(accountAddress).balance)
        assertEquals(50.pounds, pots[destination].balance)
        assertEquals(10.pounds, pots[remainder].balance)
    }

    @Test
    fun `not enough funds to distribute`() {
        val account = bank.createAccount(CreateAccountRequest(accountAddress, 0.pounds))
        val source = PotName("source")
        val destination = PotName("destination")
        val remainder = PotName("remainder")
        bank.createPot(CreatePotRequest(account, source, 100.pounds))
        bank.createPot(CreatePotRequest(account, destination, 0.pounds))
        bank.createPot(CreatePotRequest(account, remainder, 0.pounds))
        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = source,
            deposits = listOf(Deposit(to = destination, amount = 70.pounds)),
            keepInMainAccount = 30.pounds,
            remainder = Remainder(to = remainder, atLeast = 10.pounds)
        )

        val exception = assertThrows<IllegalArgumentException> {
            distributeFunds(bank, manifest)
        }
        assertEquals("Not enough funds: required=£110.00 available=£100.00", exception.message)
    }
}