@file:Suppress("DEPRECATION")

package com.radzdev.radzweblibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast

class PopUpActivity : Activity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create WebView programmatically
        webView = WebView(this)
        webView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Set up WebView settings
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true // Enable JavaScript

        // Set up WebViewClient to handle URLs within the WebView itself
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap
            ) {
                super.onPageStarted(view, url, favicon)
                // Optionally, show a progress bar or something to indicate loading.
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                // All content for the page has been loaded
                // If the URL contains a specific string, close the pop-up
                if (url.contains("your_specific_url_or_condition")) {
                    closePopUp("Page loaded and activity is closing.")
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                // Handle error - Close the pop-up or show an error message
                Toast.makeText(
                    this@PopUpActivity,
                    "Error loading page: $description", Toast.LENGTH_SHORT
                ).show()
                closePopUp("Error occurred, activity is closing.")
            }
        }

        // Prevent pop-ups or new windows from opening in the WebView
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                // Prevent opening new windows (pop-ups)
                Toast.makeText(
                    this@PopUpActivity,
                    "Blocked pop-up attempt.",
                    Toast.LENGTH_SHORT
                ).show()
                return false // Return false to prevent the pop-up
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                // Optionally show progress (0 to 100%)
                // This method can help track how much content has been loaded
            }
        }

        // Set the WebView as the content of the activity
        setContentView(webView)

        // Get the URL passed from the main activity
        val url = intent.getStringExtra("url")

        // Load the URL if available
        if (url != null) {
            webView.loadUrl(url)
        }

        // Auto-close the pop-up after 5 seconds (for demonstration purposes)
        Handler().postDelayed(
            { closePopUp("Activity auto-closed after 5 seconds.") },
            5000
        ) // 5000ms = 5 seconds
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent navigating back (no need for back action in the pop-up)
        closePopUp("Back pressed, activity is closing.")
    }

    // Helper method to close the pop-up activity and show a toast message
    private fun closePopUp(message: String) {
        Toast.makeText(this@PopUpActivity, message, Toast.LENGTH_SHORT).show()
        finish() // Close the activity (pop-up)
    }
}
