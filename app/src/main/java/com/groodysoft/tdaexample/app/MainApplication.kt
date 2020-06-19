package com.groodysoft.tdaexample.app

import android.app.Application
import com.google.gson.Gson
import com.groodysoft.tdaexample.TDATokenResponse
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val LOGTAG = "TDA_DEBUG"

class MainApplication : Application() {

    init {
        instance = this
    }

    val gsonInstance: Gson = Gson()


    companion object {
        var instance: MainApplication? = null

        val context: MainApplication
            get() {
                return instance as MainApplication
            }

        val gson: Gson
            get() {
                return instance!!.gsonInstance
            }

        var tokenResponse: TDATokenResponse? = null
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // declare used Android context
            androidContext(this@MainApplication)
            // declare modules
            modules(preferencesModule)
        }
    }
}