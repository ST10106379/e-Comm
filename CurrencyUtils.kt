package com.example.e_comm

object CurrencyUtils {
    // Current conversion rate (approximate)
    private const val USD_TO_ZAR_RATE = 18.50

    fun convertUsdToZar(usdPrice: Double): Double {
        return usdPrice * USD_TO_ZAR_RATE
    }

    fun formatPrice(price: Double): String {
        val zarPrice = convertUsdToZar(price)
        return "R%.2f".format(zarPrice)
    }
}
