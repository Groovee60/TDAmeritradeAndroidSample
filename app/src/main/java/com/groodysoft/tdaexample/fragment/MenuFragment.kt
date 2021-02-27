package com.groodysoft.tdaexample.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.groodysoft.tdaexample.*
import com.groodysoft.tdaexample.api.TDALoginHelper
import com.groodysoft.tdaexample.app.LOGTAG
import com.groodysoft.tdaexample.app.MainApplication
import com.groodysoft.tdaexample.app.PREF_KEY_AUTH_CODE
import com.groodysoft.tdaexample.app.PREF_KEY_TOKEN_RESPONSE
import com.groodysoft.tdaexample.databinding.FragmentMenuBinding
import com.groodysoft.tdaexample.viewmodel.TDAInitialTokenViewModel
import com.groodysoft.tdaexample.viewmodel.TDARefreshedTokenViewModel
import org.koin.android.ext.android.inject
import java.lang.NullPointerException
import java.util.*


class MenuFragment : SubtitledFragment() {

    private val prefs by inject<SharedPreferences>()

    private var tdaLoggedIn = false

    private lateinit var binding: FragmentMenuBinding

    private enum class WaitingOnRefresh {
        None,
        GetQuotes
    }
    private var waitingOnRefresh = WaitingOnRefresh.None

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMenuBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle = getString(R.string.app_name)

        val jsonTdaTokenResponse = prefs.getString(PREF_KEY_TOKEN_RESPONSE, null)

        if (jsonTdaTokenResponse != null) {

            MainApplication.tokenResponse = MainApplication.gson.fromJson(jsonTdaTokenResponse, TDATokenResponse::class.java)

            Log.d(LOGTAG, "accessToken: ${MainApplication.tokenResponse?.accessToken}")
            Log.d(LOGTAG, "refreshToken: ${MainApplication.tokenResponse?.refreshToken}")

            tdaLoggedIn = true
        }

        enableLoginStatus()
        enableOptions()

        binding.tdaLoginButton.setOnClickListener {

            when {
                tdaLoggedIn -> {
                    tdaLogout()
                }
                requireActivity().isInternetAvailable() -> {
                    attemptTDALogin()
                }
                else -> {
                    requireActivity().showConnectivityToast()
                }
            }
        }


        binding.getQuotes.setOnClickListener {

            if (validateAccessToken()) {
                getQuotes()
            } else {
                waitingOnRefresh = WaitingOnRefresh.GetQuotes
            }
        }
    }

    private fun getQuotes() {
        findNavController().navigate(R.id.action_MenuFragment_to_GetQuotesFragment)
    }

    private fun attemptTDALogin() {

        val authCode = prefs.getString(PREF_KEY_AUTH_CODE, null)
        if (authCode == null) {
            getAuthCode()
        } else {
            Log.d(LOGTAG, "got auth code from prefs!")
            gotAuthCode(authCode)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getAuthCode() {

        val webSettings: WebSettings = binding.tdaWebView.settings
        webSettings.javaScriptEnabled = true

        binding.webViewFrame.isVisible = true
        binding.tdaWebView.loadUrl(TDALoginHelper.AUTH_CODE_URL)
        binding.tdaWebView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // this will get called a few times as the TDA stuff authenticates
                // we only care about our actual callback
                // TODO allow backing out of process
                if (url.toString().startsWith(TDALoginHelper.REDIRECT_URI)) {
                    val sanitizer = UrlQuerySanitizer(url)
                    val authCode = sanitizer.getValue("code")
                    binding.webViewFrame.isVisible = false
                    prefs.edit().putString(PREF_KEY_AUTH_CODE, authCode).apply()

                    try {
                        gotAuthCode(authCode)
                    } catch (e: Exception) {
                        val message = if (e is NullPointerException) {
                            "Did you forget to set your own authentication values in TDALoginHelper?"
                        } else {
                            "Exception: ${e.message}"
                        }
                        requireActivity().showAlert(message)
                    }
                }
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            }
        }
    }

    private fun gotAuthCode(authCode: String) {

        val tokenResponseViewModel = TDAInitialTokenViewModel(authCode)
        tokenResponseViewModel.initialToken.observe(viewLifecycleOwner, {

            storeTDATokenResponse(it)
            tdaLogin()
        })
    }

    private fun tdaLogin() {

        tdaLoggedIn = true
        enableLoginStatus()
        enableOptions()
    }

    private fun enableLoginStatus() {

        binding.tdaLoginButton.setLoggedIn(tdaLoggedIn)
        binding.tdaStatusButton.setLoggedIn(tdaLoggedIn)
    }

    private fun tdaLogout() {

        MainApplication.tokenResponse = null

        val editor = prefs.edit()
        editor.remove(PREF_KEY_AUTH_CODE)
        editor.remove(PREF_KEY_TOKEN_RESPONSE)
        editor.apply()

        tdaLoggedIn = false
        enableLoginStatus()
        enableOptions()
    }

    private fun enableOptions() {

        binding.getQuotes.isEnabled = tdaLoggedIn
    }

    private fun validateAccessToken(): Boolean {

        val timeNow = Date().time
        val acquisitionTime = MainApplication.tokenResponse?.acquireTime ?: 0
        val tokenLifeSeconds = MainApplication.tokenResponse?.expiresIn ?: 0

        val timeDiffMilliseconds = timeNow - acquisitionTime
        val timeDiffSeconds = timeDiffMilliseconds / 1000
        val needsRefresh = timeDiffSeconds > tokenLifeSeconds

        if (needsRefresh) {

            binding.progress.isVisible = true

            Toast.makeText(requireContext(), R.string.refreshing_access_token, Toast.LENGTH_SHORT).show()

            val refreshedTokenViewModel = TDARefreshedTokenViewModel()
            refreshedTokenViewModel.refreshedToken.observe(viewLifecycleOwner, {

                storeTDATokenResponse(it, true)
                binding.progress.isVisible = false

                when (waitingOnRefresh) {
                    WaitingOnRefresh.GetQuotes -> getQuotes()
                    else -> {}
                }
            })
        }

        return !needsRefresh
    }

    private fun storeTDATokenResponse(tokenResponse: TDATokenResponse, refreshing: Boolean = false) {

        val oldToken = if (refreshing) MainApplication.tokenResponse else null

        // persist it locally
        MainApplication.tokenResponse = tokenResponse

        oldToken?.let {

            MainApplication.tokenResponse?.refreshToken = it.refreshToken
            MainApplication.tokenResponse?.refreshTokenExpiresIn = it.refreshTokenExpiresIn
        }

        // update the time stamp to check for expiration
        MainApplication.tokenResponse?.acquireTime = Date().time

        // persist it permanently (or update it)
        val jsonTokenResponse = MainApplication.gson.toJson(MainApplication.tokenResponse)
        val editor = prefs.edit()
        editor.remove(PREF_KEY_AUTH_CODE) // it will never work again
        editor.putString(PREF_KEY_TOKEN_RESPONSE, jsonTokenResponse)
        editor.apply()
    }
}


