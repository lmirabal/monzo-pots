package lmirabal

class FundsDistributorTest : FundsDistributorContract() {
    override val fundsDistributor = FundsDistributorApplication(bank)
}