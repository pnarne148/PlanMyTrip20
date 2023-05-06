package com.example.planmytrip20.WebScrape

import android.util.Log
import org.jsoup.Jsoup

class WikipediaScraper(val placeName: String) {

    private val baseUrl = "https://en.wikipedia.org"
    private var imageUrl: String? = null
    private var firstParagraph: String? = null

    fun scrapeWiki() {
        val wikiUrl = "$baseUrl/wiki/$placeName"
        val doc = Jsoup.connect(wikiUrl).get()

        // Get the first image
        val imageElement = doc.select("thumbimage").first()
        imageUrl = imageElement?.absUrl("src")

        Log.d("itinerery", "scrapeWiki: "+wikiUrl)
        Log.d("itinerery", "scrapeWiki: "+imageUrl)
        // Get the first paragraph
        val paragraphElements = doc.select("#mw-content-text p")
        for (paragraphElement in paragraphElements) {
            if (paragraphElement.text().isNotEmpty()) {
                firstParagraph = paragraphElement.text()
                break
            }
        }
        Log.d("itinerery", "scrapeWiki: "+firstParagraph)
        Log.d("itinerery", "scrapeWiki: "+imageElement)

    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun getFirstParagraph(): String? {
        return firstParagraph
    }
}
