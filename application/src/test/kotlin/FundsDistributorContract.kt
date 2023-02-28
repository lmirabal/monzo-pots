package lmirabal

import lmirabal.finance.Amount
import lmirabal.finance.pounds
import lmirabal.infrastructure.CreateAccountRequest
import lmirabal.infrastructure.CreatePotRequest
import lmirabal.infrastructure.OnboardingBank
import lmirabal.infrastructure.StubBank
import lmirabal.model.Account
import lmirabal.model.AccountAddress
import lmirabal.model.Deposit
import lmirabal.model.DistributionManifest
import lmirabal.model.Pot
import lmirabal.model.PotName
import lmirabal.model.Remainder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

abstract class FundsDistributorContract {

    val bank: OnboardingBank = StubBank()
    abstract val fundsDistributor: FundsDistributor
    private val accountAddress: AccountAddress = AccountAddress("123456", "123456789")
    private val account: Account = bank.createAccount(CreateAccountRequest(accountAddress, 0.pounds))

    @Test
    fun `distribute funds`() {
        val source = bank.createPot("source", 300)
        val destination1 = bank.createPot("destination 1", 0)
        val destination2 = bank.createPot("destination 2", 0)
        val remainder = bank.createPot("remainder", 0)

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
        fundsDistributor.distribute(manifest)

        bank.asserting()
            .potBalance(source, 0)
            .accountBalance(30)
            .potBalance(destination1, 50)
            .potBalance(destination2, 70)
            .potBalance(remainder, 150)
    }

    @Test
    fun `distribute just enough funds`() {
        val source = bank.createPot("source", 90)
        val destination = bank.createPot("destination", 0)
        val remainder = bank.createPot("remainder", 0)

        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = source,
            deposits = listOf(Deposit(to = destination, amount = 50.pounds)),
            keepInMainAccount = 30.pounds,
            remainder = Remainder(to = remainder, atLeast = 10.pounds)
        )
        fundsDistributor.distribute(manifest)

        bank.asserting()
            .potBalance(source, 0)
            .accountBalance(30)
            .potBalance(destination, 50)
            .potBalance(remainder, 10)
    }

    @Test
    fun `does not distribute when not enough funds`() {
        val source = bank.createPot("source", 100)
        val destination = bank.createPot("destination", 0)
        val remainder = bank.createPot("remainder", 0)

        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = source,
            deposits = listOf(Deposit(to = destination, amount = 70.pounds)),
            keepInMainAccount = 30.pounds,
            remainder = Remainder(to = remainder, atLeast = 10.pounds)
        )
        val exception = assertThrows<IllegalArgumentException> {
            fundsDistributor.distribute(manifest)
        }

        assertEquals("Not enough funds: required=£110.00 available=£100.00", exception.message)
    }

    @Test
    fun `does not distribute when the source pot does not exist`() {
        val invalidSource = PotName("invalid-source")
        val destination = bank.createPot("destination", 0)
        val remainder = bank.createPot("remainder", 0)

        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = invalidSource,
            deposits = listOf(Deposit(to = destination, amount = 100.pounds)),
            keepInMainAccount = 100.pounds,
            remainder = Remainder(to = remainder, atLeast = 100.pounds)
        )
        val exception = assertThrows<IllegalArgumentException> {
            fundsDistributor.distribute(manifest)
        }

        bank.asserting()
            .accountBalance(0)
            .potBalance(destination, 0)
            .potBalance(remainder, 0)
        assertEquals("Manifest contains invalid pots=[invalid-source]", exception.message)
    }

    @Test
    fun `does not distribute when a destination pot does not exist`() {
        val source = bank.createPot("source", 100)
        val destination = bank.createPot("destination", 0)
        val invalidDestination = PotName("invalid-destination")
        val remainder = bank.createPot("remainder", 0)

        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = source,
            deposits = listOf(
                Deposit(to = destination, amount = 10.pounds),
                Deposit(to = invalidDestination, amount = 10.pounds)
            ),
            keepInMainAccount = 10.pounds,
            remainder = Remainder(to = remainder, atLeast = 10.pounds)
        )
        val exception = assertThrows<IllegalArgumentException> {
            fundsDistributor.distribute(manifest)
        }

        bank.asserting()
            .accountBalance(0)
            .potBalance(source, 100)
            .potBalance(destination, 0)
            .potBalance(remainder, 0)
        assertEquals("Manifest contains invalid pots=[invalid-destination]", exception.message)
    }

    @Test
    fun `does not distribute when the remainder pot does not exist`() {
        val source = bank.createPot("source", 100)
        val destination = bank.createPot("destination", 0)
        val invalidRemainder = PotName("invalid-remainder")

        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = source,
            deposits = listOf(Deposit(to = destination, amount = 10.pounds)),
            keepInMainAccount = 10.pounds,
            remainder = Remainder(to = invalidRemainder, atLeast = 10.pounds)
        )
        val exception = assertThrows<IllegalArgumentException> {
            fundsDistributor.distribute(manifest)
        }

        bank.asserting()
            .accountBalance(0)
            .potBalance(source, 100)
            .potBalance(destination, 0)
        assertEquals("Manifest contains invalid pots=[invalid-remainder]", exception.message)
    }

    @Test
    fun `does not distribute when not all pots are valid`() {
        val invalidSource = PotName("invalid-source")
        val destination = bank.createPot("destination", 0)
        val invalidDestination = PotName("invalid-destination")
        val invalidRemainder = PotName("invalid-remainder")

        val manifest = DistributionManifest(
            mainAccount = accountAddress,
            source = invalidSource,
            deposits = listOf(
                Deposit(to = destination, amount = 100.pounds),
                Deposit(to = invalidDestination, amount = 100.pounds)
            ),
            keepInMainAccount = 100.pounds,
            remainder = Remainder(to = invalidRemainder, atLeast = 100.pounds)
        )
        val exception = assertThrows<IllegalArgumentException> {
            fundsDistributor.distribute(manifest)
        }

        bank.asserting()
            .accountBalance(0)
            .potBalance(destination, 0)
        assertEquals(
            "Manifest contains invalid pots=[invalid-source, invalid-destination, invalid-remainder]",
            exception.message
        )
    }

    private fun OnboardingBank.createPot(name: String, initialBalance: Int) =
        PotName(name)
            .also { potName ->
                createPot(CreatePotRequest(account, potName, Amount.ofPounds(initialBalance)))
            }

    private fun OnboardingBank.asserting() = Asserter(getAccountBy(accountAddress), getPotsFor(account))

    private class Asserter(private val account: Account, private val pots: List<Pot>) {
        fun accountBalance(expectedBalance: Int): Asserter {
            assertEquals(Amount.ofPounds(expectedBalance), account.balance)
            return this
        }

        fun potBalance(potName: PotName, expectedBalance: Int): Asserter {
            assertEquals(Amount.ofPounds(expectedBalance), pots[potName].balance)
            return this
        }
    }
}