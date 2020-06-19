package com.groodysoft.tdaexample

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.groodysoft.tdaexample.app.MainApplication
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
fun Activity.isInternetAvailable(): Boolean {
    var result = false
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }

    return result
}

fun Activity.showAlert(messageId: Int) {
    showAlert(getString(messageId))
}

fun Activity.showAlert(message: String) {

    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)
    builder.setPositiveButton(android.R.string.ok, null)
    builder.show()
}


fun Activity.showConnectivityToast() = Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show()

fun Button.setLoggedIn(flag: Boolean) {
    text = MainApplication.context.getString(if (flag) R.string.logout else R.string.login)
}

fun ImageView.setLoggedIn(flag: Boolean) {
    setImageResource(if (flag) R.drawable.button_green else R.drawable.button_off)
}

fun adapterRowColor(position: Int): Int {
    val evenRow = (position % 2) == 0
    val colorResourceId = if (!evenRow) {
        R.color.listBackgroundEven
    } else {
        R.color.listBackgroundOdd
    }
    return ContextCompat.getColor(MainApplication.context, colorResourceId)
}