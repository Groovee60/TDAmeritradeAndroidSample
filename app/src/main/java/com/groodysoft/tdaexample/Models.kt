package com.groodysoft.tdaexample

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.groodysoft.tdaexample.app.MainApplication

data class TDATokenResponse(

    // updatable attributes
    @SerializedName("refresh_token")
    var refreshToken: String,
    @SerializedName("refresh_token_expires_in")
    var refreshTokenExpiresIn: Int,

    // const attributes
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("token_Type")
    val tokenType: String,
    val scope:String,

    // application-added attributes
    var acquireTime: Long
)



data class TDAQuote (
    val symbol: String,

    @SerializedName(value="bidPrice", alternate = ["bidPriceInDouble"])
    val bidPrice: Double,
    @SerializedName(value="askPrice", alternate = ["askPriceInDouble"])
    val askPrice: Double,
    @SerializedName(value="lastPrice", alternate = ["lastPriceInDouble"])
    val lastPrice: Double,
    @SerializedName(value="netChange", alternate = ["changeInDouble"])
    val netChange: Double
) {

    val formattedBid: SpannableString
        get() {
            return bidPrice.formatPrice()
        }

    val formattedAsk: SpannableString
        get() {
            return askPrice.formatPrice()
        }

    val formattedLast: SpannableString
        get() {
            return lastPrice.formatPrice()
        }

    val formattedChange: SpannableString
        get() {
            return netChange.formatPrice(true)
        }
}

fun Double.formatPrice(styled: Boolean = false): SpannableString {

    val result = SpannableString("%.2f".format(this))

    if (styled) {

        val colorResId = if (this >= 0.0) R.color.chart_green else R.color.chart_red
        val priceColor = ContextCompat.getColor(MainApplication.context, colorResId)
        result.setSpan(
            ForegroundColorSpan(priceColor),
            0,
            result.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }

    return result
}
