package com.example.planmytrip20.ui.home.DetailFragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.planmytrip20.R

class DetailFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        // Initialize the WebView
        webView = view.findViewById(R.id.web_view)
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.javaScriptEnabled = true

        // Set up the WebViewClient to handle page loading and URL redirection
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // Redirect all links to the WebView
                view.loadUrl(url)
                return true
            }
        }

        // Load the destination details in the WebView
        val destinationName = arguments?.getString("destinationName") ?: ""
        val url = Uri.parse("https://www.lonelyplanet.com/search?q=$destinationName")
        webView.loadUrl(url.toString())

        return view
    }

}
