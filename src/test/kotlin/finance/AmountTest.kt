package lmirabal.finance

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AmountTest {

    @Test
    fun `can be built from Pounds`() {
        assertEquals(Amount(100), 1.pound)
        assertEquals(Amount(1_000), 10.pounds)
        assertEquals(Amount(1_000), Amount.ofPounds(10))
        assertEquals(Amount(1_000), Amount.ofPounds(10L))
    }

    @Test
    fun `can be built from Pounds and pence`() {
        assertEquals(Amount(1_010), Amount.ofPounds(10, 10))
    }

    @ParameterizedTest
    @CsvSource("200,50,150", "100,0,100", "0,0,0")
    fun `can be added up`(expected: Long, pence1: Long, pence2: Long) {
        assertEquals(Amount(expected), Amount(pence1) + Amount(pence2))
    }

    @ParameterizedTest
    @CsvSource("30,150,120", "100,100,0", "0,0,0", "-20,120,140")
    fun `can be subtracted`(expected: Long, pence1: Long, pence2: Long) {
        assertEquals(Amount(expected), Amount(pence1) - Amount(pence2))
    }

    @ParameterizedTest
    @CsvSource("-100,100", "100,-100", "0,0")
    fun `can be negated`(expected: Long, pence: Long) {
        assertEquals(Amount(expected), -Amount(pence))
    }

    @ParameterizedTest
    @CsvSource("£1.11,111", "£1.00,100", "£10.00,1000", "£10.10,1010,", "£-12.34,-1234", "£1000.00,100_000")
    fun `toString is formatted`(expected: String, pence: Long) {
        assertEquals(expected, Amount(pence).toString())
    }
}