@file:Suppress("DEPRECATION")

package com.radzdev.radzweblibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.*
import android.widget.FrameLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class radzweb : Activity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var webView: WebView
    private lateinit var loadingAnimation: LottieAnimationView
    private var adServersList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        // Initialize views
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        webView = findViewById(R.id.webview)
        loadingAnimation = findViewById(R.id.loading_animation)

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            loadingAnimation.visibility = View.VISIBLE
            webView.reload()
        }

        // Set up WebView and fetch ad block list
        setupWebView()
        fetchAdBlockList()

        // Get the URL from the Intent
        val url = intent.getStringExtra("url") ?: "https://google.com/"

        // Load content in WebView
        loadContent(url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webSettings = webView.settings
        webSettings.apply {
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false)

        // Set WebViewClient and WebChromeClient
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                customView = view
                customViewCallback = callback
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                val frameLayout = findViewById<FrameLayout>(android.R.id.content)
                frameLayout.addView(
                    customView, FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
            }

            override fun onHideCustomView() {
                val frameLayout = findViewById<FrameLayout>(android.R.id.content)
                frameLayout.removeView(customView)
                customView = null
                customViewCallback?.onCustomViewHidden()
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    private fun loadContent(url: String) {
        if (!isConnectedToInternet()) {
            // If no internet connection, load the 404.html from assets
            try {
                val htmlData = "404.html".loadHTMLFromAssets()
                webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
            } catch (e: IOException) {
                Log.e(TAG, "Error loading 404.html", e)
            }
        } else {
            // If connected, load the URL
            webView.loadUrl(url)
        }
    }

    private fun fetchAdBlockList() {
        Thread {
            try {
                val url = URL("https://raw.githubusercontent.com/Radzdevteam/test/refs/heads/main/adhost1")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                connection.inputStream.bufferedReader().use { reader ->
                    val tempList = reader.lineSequence()
                        .filter { it.isNotBlank() && !it.startsWith("#") }
                        .map { it.trim() }
                        .toList()
                    adServersList = tempList
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching ad block list", e)
            }
        }.start()
    }

    @SuppressLint("MissingPermission")
    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Function to load HTML file from assets folder
    private fun String.loadHTMLFromAssets(): String {
        val assetManager = assets
        val inputStream = assetManager.open(this)
        return inputStream.bufferedReader().use { it.readText() }
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            loadingAnimation.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            return if (url.startsWith("http") || url.startsWith("https")) {
                loadingAnimation.visibility = View.VISIBLE
                false
            } else {
                loadingAnimation.visibility = View.GONE
                true
            }
        }

        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            val url = request.url.toString()
            return if (adServersList.any { url.contains(it) }) {
                WebResourceResponse("text/plain", "utf-8", null)
            } else {
                super.shouldInterceptRequest(view, request)
            }
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed() // Be cautious when proceeding with SSL errors
        }

        override fun onReceivedError(
            view: WebView, request: WebResourceRequest, error: WebResourceError
        ) {
            if (!isConnectedToInternet()) {
                try {
                    val htmlData = "404.html".loadHTMLFromAssets()
                    webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
                } catch (e: IOException) {
                    Log.e(TAG, "Error loading 404.html", e)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("webview_state", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(LAST_URL_KEY, webView.url)
        editor.apply()
    }

    companion object {
        private const val LAST_URL_KEY = "last_url"
        private const val TAG = "AdBlockApp"
    }
}

