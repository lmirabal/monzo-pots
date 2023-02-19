# Monzo pots

A kotlin solution that uses the Monzo APIs to automate moving funds between pots.

## Distribute funds

It allows to take funds from a source pot and distribute them into other pots.

The intention is to provide a salary sorter like functionality that can be triggered on demand. The source of the funds
is not an incoming payment but a pot.

### Why?

I split my income into pots to have a better control of my budget. This is easy enough using the salary sorter, but I
get paid in the middle of the month. I prefer to budget by calendar month, so it's much easier if the income is release
on the first day of the month. The way I achieve that is by moving the full amount to a "buffer" pot that then I
distribute on the last day of each month.

### How it works?

You provide a manifest indicating the following:

- Source pot: the pot where the funds come from.
- Deposit pots: the pots where the funds are distributed to.
- Keep in main account: amount that should be withdrawn from the source pot, but it's not expected to move to other pot.
  This is for certain expenses that are paid directly from the main account.
- Remainder pot: special pot where you define a minimum amount that it should receive, and where the remainder funds get
  moved to. This is to cater for months when the income varies, so that the deposit pots don't need to match the source.
  This feature is intended for savings, where you set a minimum you want to save each month, but if you have more funds
  available, then you can save more.

The manifest in code looks like:

```kotlin
DistributionManifest(
    mainAccount = AccountAddress("123456", "123456789"),
    source = PotName("source"),
    deposits = listOf(Deposit(to = PotName("destination"), amount = 10.pounds)),
    keepInMainAccount = 10.pounds,
    remainder = Remainder(to = PotName("Savings"), atLeast = 10.pounds)
)
```

It currently can only be executed via console grabbing an auth token from the Monzo developer portal.