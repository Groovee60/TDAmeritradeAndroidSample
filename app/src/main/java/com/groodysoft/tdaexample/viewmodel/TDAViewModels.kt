package com.groodysoft.tdaexample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.groodysoft.tdaexample.TDAQuote
import com.groodysoft.tdaexample.TDATokenResponse
import com.groodysoft.tdaexample.api.TDARepository
import com.groodysoft.tdaexample.app.MainApplication
import kotlinx.coroutines.Dispatchers

class TDAInitialTokenViewModel(authCode: String) : ViewModel() {

    val initialToken: LiveData<TDATokenResponse> = liveData(Dispatchers.IO) {

        val retrievedToken = TDARepository.getInitialToken(authCode)
        emit(retrievedToken)
    }
}

class TDARefreshedTokenViewModel() : ViewModel() {

    val refreshedToken: LiveData<TDATokenResponse> = liveData(Dispatchers.IO) {

        val retrievedToken = TDARepository.getRefreshedToken()
        emit(retrievedToken)
    }
}

class TDAQuoteViewModel(commaSeparatedSymbols: String) : ViewModel() {

    val quotes: LiveData<ArrayList<TDAQuote>> = liveData(Dispatchers.IO) {

        val quoteMap = TDARepository.getQuotes(commaSeparatedSymbols, "Bearer ${MainApplication.tokenResponse?.accessToken}")
        emit(ArrayList(quoteMap.values))
    }
}
