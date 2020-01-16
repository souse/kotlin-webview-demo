package com.example.hello_kotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : Activity() {

    private var url: String = "https://www.baidu.com/"
    lateinit var webView: WebView
    lateinit var buttonLoad: Button
    lateinit var buttonBack: Button
    lateinit var buttonForward: Button
    lateinit var buttonGo: Button
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        buttonLoad = findViewById(R.id.buttonLoad)
        buttonBack = findViewById(R.id.buttonBack)
        buttonForward = findViewById(R.id.buttonForward)
        buttonGo = findViewById(R.id.buttonGo)
        progressBar = findViewById(R.id.progressBar)

        // Get the web view settings instance
        val settings = webView.settings

        // Enable java script in web view
        settings.javaScriptEnabled = true

        // Enable and setup web view cache
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCachePath(cacheDir.path)

        // Enable zooming in web view
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = true

        // Zoom web view text
        settings.textZoom = 100

        // Enable disable images in web view
        settings.blockNetworkImage = false
        // Whether the WebView should load image resources
        settings.loadsImagesAutomatically = true

        // More web view settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true  // api 26
        }

        //settings.pluginState = WebSettings.PluginState.ON
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        //        settings.mediaPlaybackRequiresUserGesture = false

        // More optional settings, you can enable it by yourself
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        //        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true

        // WebView settings
        webView.fitsSystemWindows = true

        /*
        if SDK version is greater of 19 then activate hardware acceleration
        otherwise activate software acceleration
        */
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // Set web view client
        webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                // Page loading started
                // Do something
                //                toast("Page loading.")
                //                Toast.makeText(this,"page loading.", Toast.LENGTH_SHORT).show()

                Log.println(1, "webview", "start")

                // Enable disable back forward button
                buttonBack.isEnabled = webView.canGoBack()
                buttonForward.isEnabled = webView.canGoForward()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.println(1, "webview", "finished")

                // Enable disable back forward button
                buttonBack.isEnabled = webView.canGoBack()
                buttonForward.isEnabled = webView.canGoForward()
                progressBar.progress = 0
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                println(error)
            }
        }

        // Set web view chrome client
        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
            }
        }

        // webView.loadUrl(url)

        // Load button click listener
        buttonLoad.setOnClickListener{
            webView.loadUrl(url)
        }

        // Back button click listener
        buttonBack.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        // Forward button click listener
        buttonForward.setOnClickListener {
            if (webView.canGoForward()) {
                webView.goForward()
            }
        }

        buttonGo.setOnClickListener {
            var intent = Intent(this@MainActivity, ChcActivity::class.java)
            var bundle = Bundle()

            bundle.putString("url", "http://10.177.11.248:8080/#/fixed1")
            intent.putExtras(bundle)

            startActivity(intent)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        println("")

        return false
    }

    // Method to show app exit dialog
    private  fun showAppExitDialog() {
        var builder = AlertDialog.Builder(this)

        builder.setTitle("Please confirm")
        builder.setMessage("No back history found, want to exit the app?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes") { _, _ ->
            // Do something when user want to exit the app
            // Let allow the system to handle the event, such as exit the app
            super@MainActivity.onBackPressed()
        }

        builder.setNegativeButton("No") {_, _ ->
            Log.println(1, "webView", "setNegativeButton")
            Toast.makeText(this,"thank you", Toast.LENGTH_SHORT).show()
        }

        // Create the alert dialog using alert dialog builder
        val dialog = builder.create()
        // Finally, display the dialog when user press back button
        dialog.show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            Toast.makeText(this,"Going to back history", Toast.LENGTH_SHORT).show()
        } else {
            showAppExitDialog()
        }
    }
}

// Extension function to show toast message
