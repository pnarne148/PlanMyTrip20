package com.example.planmytrip20.WebScrape

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class WikipediaApi {
    companion object {
        private const val BASE_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages&piprop=original&titles="

        fun getImageUrlFromWikipedia(place: String): String? {
            val client = OkHttpClient()
            val url = BASE_URL + place.replace(" ", "%20")
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) return null

                val jsonString = response.body?.string() ?: return null
                val jsonObject = JSONObject(jsonString)
                val pages = jsonObject.getJSONObject("query").getJSONObject("pages")
                val pageId = pages.keys().next()
                val imageUrl = pages.getJSONObject(pageId).optJSONObject("original")?.getString("source")
                return imageUrl
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null
        }

        fun getURL(place: String): String {
            val url = BASE_URL + place.replace(" ", "%20")
            return url
        }


        fun getFirstParagraphFromWikipedia(place: String): String? {
            val client = OkHttpClient()
            val url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&explaintext=true&titles=" + place.replace(" ", "%20")
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) return null

                val jsonString = response.body?.string() ?: return null
                val jsonObject = JSONObject(jsonString)
                val pages = jsonObject.getJSONObject("query").getJSONObject("pages")
                val pageId = pages.keys().next()
                val extract = pages.getJSONObject(pageId).optString("extract")
                val firstParagraph = extract.split("\n")[0]
                return firstParagraph
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null
        }

    }
}
