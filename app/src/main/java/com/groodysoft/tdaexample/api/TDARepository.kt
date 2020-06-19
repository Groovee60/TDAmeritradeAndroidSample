package com.groodysoft.tdaexample.api

import com.google.gson.GsonBuilder
import com.groodysoft.tdaexample.TDAQuote
import com.groodysoft.tdaexample.TDATokenResponse
import com.groodysoft.tdaexample.app.MainApplication
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TDAmeritradeAPI {

    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun postAccessToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
        @Field("access_type") accessType: String,
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("redirect_uri") redirectURI: String) : TDATokenResponse

    @GET("v1/marketdata/quotes")
    suspend fun getQuotes(@Query("symbol") commaSeparatedSymbols: String, @Header("Authorization") auth: String) : Map<String, TDAQuote>
}

object TDARepository {

    private var client: TDAmeritradeAPI = tdaAPI

    suspend fun getInitialToken(authCode: String) = client.postAccessToken(
            grantType = "authorization_code",
            refreshToken = "",
            accessType = "offline",
            code = authCode,
            clientId = TDALoginHelper.CLIENT_ID,
            redirectURI = TDALoginHelper.REDIRECT_URI)

    suspend fun getRefreshedToken() = client.postAccessToken(
        grantType = "refresh_token",
        refreshToken = MainApplication.tokenResponse?.refreshToken ?: String(),
        accessType = String(),
        code = String(),
        clientId = TDALoginHelper.CLIENT_ID,
        redirectURI = TDALoginHelper.REDIRECT_URI)

    suspend fun getQuotes(commaSeparatedSymbols: String, auth: String) = client.getQuotes(commaSeparatedSymbols, auth)
}


val tdaAPI:TDAmeritradeAPI by lazy {

    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
//        .addInterceptor { chain ->
//            val original = chain.request()
//            val requestBuilder = original.newBuilder().header("Authorization", "Bearer ${MainApplication.accessToken}")
//            val request = requestBuilder.build()
//            chain.proceed(request)
//        }
        .build()

    Retrofit.Builder()
        .baseUrl("https://api.tdameritrade.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build().create(TDAmeritradeAPI::class.java)
}





