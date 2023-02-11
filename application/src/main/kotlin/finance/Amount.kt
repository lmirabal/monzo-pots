package lmirabal.finance

import kotlin.math.absoluteValue

val Int.pounds: Amount
    get() = Amount.ofPounds(this)

val Int.pound: Amount
    get() {
        require(this == 1)
        return Amount.ONE
    }

class Amount(val pence: Long) {

    operator fun plus(that: Amount) = Amount(this.pence + that.pence)
    operator fun minus(that: Amount) = Amount(this.pence - that.pence)
    operator fun unaryMinus() = Amount(-pence)

    operator fun compareTo(that: Amount) = this.pence.compareTo(that.pence)

    override fun toString(): String {
        val pounds = this.pence.div(100)
        val pence = "%02d".format(this.pence.absoluteValue.rem(100))
        return "£$pounds.$pence"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Amount

        if (pence != other.pence) return false

        return true
    }

    override fun hashCode(): Int {
        return pence.hashCode()
    }


    companion object {
        val ZERO = Amount(0)
        val ONE = Amount(100)
        fun ofPounds(pounds: Int) = ofPounds(pounds.toLong())
        fun ofPounds(pounds: Long) = Amount(pounds * 100)
        fun ofPounds(pounds: Long, pence: Long): Amount {
            require(pence < 100L) { "Max is 99 pence, but got: $pence" }
            return Amount((pounds * 100) + pence)
        }
    }
}