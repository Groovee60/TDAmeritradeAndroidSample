package com.groodysoft.tdaexample.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

const val PREF_KEY_AUTH_CODE = ".PREF_KEY_AUTH_CODE"
const val PREF_KEY_TOKEN_RESPONSE = ".PREF_KEY_TOKEN_RESPONSE"

val preferencesModule = module {
    single { provideSettingsPreferences(androidApplication()) }
}

private val prefsPackageName = "${MainApplication.context.packageName}.preferences"

private fun provideSettingsPreferences(app: Application): SharedPreferences =
    app.getSharedPreferences(prefsPackageName, Context.MODE_PRIVATE)