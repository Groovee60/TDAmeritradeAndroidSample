package com.groodysoft.tdaexample.api

object TDALoginHelper {

    // TODO Important
    // populate your redirect uri and client ID exactly as found in the app created at https://developer.tdameritrade.com/
    const val REDIRECT_URI = ""
    const val CLIENT_ID = ""

    const val AUTH_CODE_URL = "https://auth.tdameritrade.com/auth?response_type=code&redirect_uri=$REDIRECT_URI&client_id=$CLIENT_ID@AMER.OAUTHAP"
}