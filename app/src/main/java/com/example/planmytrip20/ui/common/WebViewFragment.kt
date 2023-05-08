package com.example.planmytrip20.ui.common

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
import com.example.planmytrip20.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {

    private lateinit var binding: FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentWebViewBinding.inflate(layoutInflater, container, false)

        binding.placeDetailWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.placeDetailWebView.settings.javaScriptEnabled = true

        binding.placeDetailWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // Redirect all links to the WebView
                view.loadUrl(url)
                return true
            }
        }

        val url = Uri.parse("https://www.google.com/maps/place/Boston+University/@42.3505036,-71.1079794,17z/data=!3m1!4b1!4m6!3m5!1s0x89e379f063e53817:0x2b346e00e0a3bec8!8m2!3d42.3504997!4d-71.1053991!16zL20vMGdsNV8")
        binding.placeDetailWebView.loadUrl(url.toString())

        return binding.root
    }

    companion object {
        const val TAG = "Web View Fragment"
    }
}